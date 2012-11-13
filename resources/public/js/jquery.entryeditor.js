(function($){

  var typeTypes = [];

  function init() {
      var $typesCombo = this.find("#techtype");
      
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
  
  function initTypesCombo($typesCombo) {
      if (($typesCombo).children("option").length !== 0) {
          return;
      }

      $.each(typeTypes, function(i, type) {
          $typesCombo.append($("<option></option>").val(type.id)
                                                   .text(type.name)
                                                   .css("background-color", type.colour)
                                                   .data("colour", type.colour))
                     .on("change", function() {
                         $typesCombo.css("background", $(this.options[this.selectedIndex]).data("colour"));
                     });
      });
  }

  function show($entryView) {
      var entryId = $entryView.data("entryId");

      initTypesCombo(this.find("#techtype"));

      this.find("#id").val(entryId);
      this.find("#name").val($entryView.text());
      this.find("#techtype").val(0).change();
      this.find("#description").val("");
      this.find("#notes").val("");
      this.dialog("open");
  }

  function exec(f, $targets, args) {
      return $targets.each(function() {
          f.apply($(this), args);
      });
  }

  function hintTypes(types) {
      typeTypes = types;
  }
  
  $.getJSON("/techtypes", hintTypes);

  $.fn.entryeditor = function(method, $entryView) {
      if (method === "show") {
          return exec(show, this, [$entryView]);
      }
      return exec(init, this);
  };
})(jQuery);