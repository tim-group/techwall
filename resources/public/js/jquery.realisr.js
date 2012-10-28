(function( $ ) {
  $.fn.realise = function() {
    return this
      .filter(".template")
      .clone()
      .removeClass("template")
      .appendTo(this.parent());
  };
})( jQuery );