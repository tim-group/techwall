$(document).ready(function() {
    var wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;
    if (wallId) {
        $.getJSON("/walls/" + wallId + "/wall.json", function(wallJson) {
        });
    }

    var allTechnologies = ["java", "scala", "ruby", "erlang"];
    var columns = [{"name": "Radical",    "entries": ["erlang"]},
                   {"name": "Tentative",  "entries": ["clojure", "play"]},
                   {"name": "Adopted",    "entries": ["java", "junit", "guava"]},
                   {"name": "Deprecated", "entries": ["easymock", "test objects"]},
                   {"name": "Obsolete",   "entries": ["lambdaj"]}];

    function addEntry($columnView, name) {
        $columnView.find(".entry").realise().text(name);
    }
    
    $.each(columns, function(i, column) {
        var columnView = $(".column").realise();
        columnView.find(".name").text(column.name);
        $.each(column.entries, function(j, entry) {
            addEntry(columnView, entry);
        });
    });

    $(".connectedSortable").sortable({
        connectWith: ".connectedSortable"
    }).disableSelection();

    $(".tags").autocomplete({
        source: allTechnologies,
    }).keypress(function(e) {
        if (e.keyCode === $.ui.keyCode.ENTER) {
            var $el = $(this);
            addEntry($el.closest(".column"), $el.val());
            $el.val("");
        }
    });
});