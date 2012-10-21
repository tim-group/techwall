$(document).ready(function() {
    var wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;
    if (wallId) {
        $.getJSON("/walls/" + wallId + "/wall.json", function(wallJson) {
        });
    }

    $(".connectedSortable").sortable({
        connectWith: ".connectedSortable"
    }).disableSelection();

    $(".tags").autocomplete({
        source: ["java", "scala", "ruby", "erlang"],
    }).keypress(function(e) {
        if (e.keyCode === $.ui.keyCode.ENTER) {
            var $el = $(this);
            $el.closest(".column").children(".connectedSortable").append($("<li class=\"ui-state-highlight\"></li>").text($el.val()));
            $el.val("");
        }
    });
});