import urlparse
from urllib import quote as urlquote
from twisted.internet import reactor, ssl
from twisted.web import proxy, server, static, resource

class MyProxyResource(proxy.ReverseProxyResource):
    hostMap = {
        "preview-us.codenvy.ctof.intuit.com": "preview.us.intuit.codenvy.com",
    "preview-asia.codenvy.ctof.intuit.com":"preview.asia.cf.codenvy-stg.com"
}
    proxyClientFactoryClass = proxy.ProxyClientFactory

    def __init__(self, path, reactor=reactor):
        resource.Resource.__init__(self)
        self.path = path
        self.reactor = reactor

    def getChild(self, path, request):
        if path == 'update_url':
            print 'Updating url', request.getRequestHostname(), 'to', request.uri[12:]
            MyProxyResource.hostMap[request.getRequestHostname()] = request.uri[12:]
            resource = static.Data("<html><body>Mapped: %s to %s</body></html>" % (request.getRequestHostname(), MyProxyResource.hostMap[request.getRequestHostname()]), "text/html");
            resource.isLeaf = True
            return resource
        if path == 'update_uri':
            host = request.uri[12:].split('..')[0]
            uri = request.uri[12:].split('..')[1]
            print 'Updating uri', host, 'to', uri
            MyProxyResource.hostMap[host] = uri
            resource = static.Data("<html><body>Mapped: %s to %s</body></html>" % (host, MyProxyResource.hostMap[host]), "text/html");
            resource.isLeaf = True
            return resource

        return MyProxyResource(self.path + '/' + urlquote(path, safe=""), self.reactor)

    def render(self, request):
        host = MyProxyResource.hostMap.get(request.getRequestHostname(), 'www.intuit.com')
        port = 80
        print 'Rendering...',request.uri, 'to', host,
        request.received_headers['host'] = host
        request.content.seek(0, 0)
        qs = urlparse.urlparse(request.uri)[4]
        if qs:
            rest = self.path + '?' + qs
        else:
            rest = self.path
        clientFactory = self.proxyClientFactoryClass(
          request.method, rest, request.clientproto,
      request.getAllHeaders(), request.content.read(), request)
        self.reactor.connectTCP(host, port, clientFactory)
        print 'Success'
        return server.NOT_DONE_YET

class ChainedOpenSSLContextFactory(ssl.DefaultOpenSSLContextFactory):
    def __init__(self, privateKeyFileName, certificateChainFileName,
                 sslmethod=ssl.SSL.SSLv23_METHOD):
        """
        @param privateKeyFileName: Name of a file containing a private key
        @param certificateChainFileName: Name of a file containing a certificate chain
        @param sslmethod: The SSL method to use
        """
        self.privateKeyFileName = privateKeyFileName
        self.certificateChainFileName = certificateChainFileName
        self.sslmethod = sslmethod
        self.cacheContext()

    def cacheContext(self):
        ctx = ssl.SSL.Context(self.sslmethod)
        ctx.use_certificate_chain_file(self.certificateChainFileName)
        ctx.use_privatekey_file(self.privateKeyFileName)
        self._context = ctx

site = server.Site(MyProxyResource(''))
reactor.listenTCP(80, site)
reactor.listenSSL(443, site, ChainedOpenSSLContextFactory('server.pem','server.crt'))
reactor.run()
