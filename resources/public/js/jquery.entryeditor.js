(function($){

  function init() {
      this.dialog({
          "autoOpen": false,
          "modal": true,
          "buttons": {
              "Save": function() {
                  $(this).dialog("close");
              },
              "Cancel": function() {
                  $(this).dialog("close");
              }
          },
      });
  }
    
  function show($entryView) {
      var entryId = $entryView.data("entryId");

      this.find("#id").val(entryId);
      this.find("#name").val($entryView.text());
      this.find("#techtype").val("");
      this.find("#description").val("");
      this.find("#notes").val("");
      this.dialog("open");
  }

  function exec(f, $targets, args) {
      return $targets.each(function() {
          f.apply($(this), args);
      });
  }

  $.fn.entryeditor = function(method, $entryView) {
      if (method === "show") {
          return exec(show, this, [$entryView]);
      }
      return exec(init, this);
  };
})(jQuery);