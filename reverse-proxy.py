import urlparse
from urllib import quote as urlquote
from twisted.internet import reactor, ssl
from twisted.web import proxy, server, static, resource

class MyProxyResource(proxy.ReverseProxyResource):
    hostMap = {}
    proxyClientFactoryClass = proxy.ProxyClientFactory

    def __init__(self, path, reactor=reactor):
        resource.Resource.__init__(self)
        self.path = path
        self.reactor = reactor

    def getChild(self, path, request):
        if path == 'update_url':
            MyProxyResource.hostMap[request.getRequestHostname()] = request.uri[12:]
            resource = static.Data("<html><body>Mapped: %s to %s</body></html>" % (request.getRequestHostname(), MyProxyResource.hostMap[request.getRequestHostname()]), "text/html");
            resource.isLeaf = True
            return resource
        return MyProxyResource(self.path + '/' + urlquote(path, safe=""), self.reactor)

    def render(self, request):
        host = MyProxyResource.hostMap.get(request.getRequestHostname(), 'www.purplemagma.com')
        port = 80
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
        return server.NOT_DONE_YET

site = server.Site(MyProxyResource(''))
reactor.listenTCP(80, site)
reactor.listenSSL(443, site, ssl.DefaultOpenSSLContextFactory('server.pem','server.crt'))
reactor.run()
