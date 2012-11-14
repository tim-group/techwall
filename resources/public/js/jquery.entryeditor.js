(function($){

  var typeTypes = [],
      successCallback = function() {};

  function save($dialog) {
      var entryId = $dialog.find("#id").val();
      
      $.ajax({
          "type": "PUT",
          "url": "/technology/" + entryId,
          "data": {
              "name":        $dialog.find("#name").val(),
              "techtypeid":  $dialog.find("#techtype").val(),
              "description": $dialog.find("#description").val()
          },
          "success": successCallback,
          "dataType": "json"
      });
      $.ajax({
          "type": "PUT",
          "url": "/wall/" + $dialog.find("#wallId").val() + "/technology/" + entryId + "/note",
          "data": {
              "note": $dialog.find("#notes").val()
          },
          "dataType": "json"
      });
  }

  function init() {
      var $typesCombo = this.find("#techtype");
      
      this.dialog({
          "autoOpen": false,
          "modal": true,
          "width": 600,
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

  function show(wallId, entryId, successfulSaveCallback) {
      var $dialog = this;
      successCallback = successfulSaveCallback;

      $dialog.find("#id").val(entryId);
      $dialog.find("#wallId").val(wallId);
      $dialog.find("#notes").val("").prop('disabled', true);
      
      initTypesCombo($dialog.find("#techtype"));
      
      $.getJSON("/technology/" + entryId, function(technology) {
          $dialog.find("#name").val(technology.name);
          $dialog.find("#techtype").val(technology.techtypeid).change();
          $dialog.find("#description").val(technology.description);
          $dialog.dialog("open");
      });
      $.getJSON("/wall/" + wallId + "/technology/" + entryId + "/note", function(techwallnote) {
          var note = techwallnote ? techwallnote.note : "";
          $dialog.find("#notes").val(note).prop('disabled', false);
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

  $.fn.entryeditor = function(method, wallId, entryId, saveCallback) {
      if (method === "show") {
          return exec(show, this, [wallId, entryId, saveCallback]);
      }
      return exec(init, this);
  };
})(jQuery);