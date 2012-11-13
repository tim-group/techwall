(function($){

  var methods = {
      "init" : function() {
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
      },
      "show" : function($entryView) {
          var entryId = $entryView.data("entryId");
          $("#edit-tech").find("#id").val(entryId);
          $("#edit-tech").find("#name").val($entryView.text());
          $("#edit-tech").find("#techtype").val("");
          $("#edit-tech").find("#description").val("");
          $("#edit-tech").find("#notes").val("");
          $("#edit-tech").dialog("open");

          this.dialog("open");
      }
  };

  function exec(f, $targets, args) {
      return $targets.each(function() {
          f.apply($(this), args);
      });
  }

  $.fn.entryeditor = function(method) {
      if (methods[method]) {
          return exec(methods[method], this, Array.prototype.slice.call(arguments, 1))
      } else if (typeof method === "object" || ! method) {
          return exec(methods.init, this, arguments);
      } else {
          $.error("Method " +  method + " does not exist on jQuery.entryeditor");
      }
  };
})(jQuery);