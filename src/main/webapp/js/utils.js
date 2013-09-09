Function.prototype.curry = function() {
    var fn = this, args = Array.prototype.slice.call(arguments);
    return function() {
      return fn.apply(this, args.concat(
        Array.prototype.slice.call(arguments)));
    };
}

function restCall(operation) {
  $('#progress').text('Start '+operation);
  $.get('rest/'+operation, function(data) {
    $('#extraInfo').html(data);
    $('#progress').text('Completed '+operation);
  });
}
