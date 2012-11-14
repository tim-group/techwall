(function($){

  var typeTypes = [],
      successCallback = function() {};

  function save($dialog) {
      $.ajax({
          "type": "PUT",
          "url": "/technology/" + $dialog.find("#id").val(),
          "data": {
              "name":        $dialog.find("#name").val(),
              "techtypeid":  $dialog.find("#techtype").val(),
              "description": $dialog.find("#description").val()
          },
          "success": successCallback,
          "dataType": "json"
      });
  }
  
  function init() {
      var $typesCombo = this.find("#techtype");
      
      this.dialog({
          "autoOpen": false,
          "modal": true,
          "buttons": {
              "Save": function() {
                  save($(this));
                  $(this).dialog("close");
              },
              "Cancel": function() {
                  $(this).dialog("close");
              }
          },
      });
  }
  
  function initTypesCombo($typesCombo) {
      if ($typesCombo.children("option").length !== 0) {
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

  function show(entryId, successfulSaveCallback) {
      var $dialog = this;
      successCallback = successfulSaveCallback;

      initTypesCombo($dialog.find("#techtype"));
      $.getJSON("/technology/" + entryId, function(technology) {
          $dialog.find("#id").val(entryId);
          $dialog.find("#name").val(technology.name);
          $dialog.find("#techtype").val(technology.techtypeid).change();
          $dialog.find("#description").val(technology.description);
          $dialog.find("#notes").val("");
          $dialog.dialog("open");
      });
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

  $.fn.entryeditor = function(method, entryId, saveCallback) {
      if (method === "show") {
          return exec(show, this, [entryId, saveCallback]);
      }
      return exec(init, this);
  };
})(jQuery);