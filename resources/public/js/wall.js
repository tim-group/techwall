$(document).ready(function() {
    var tagNames = [],
        wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;

    if (!wallId) {
        window.location.replace("/");
    }

    function addEntry($categoryView, name) {
        return $categoryView.find(".entry").realise().text(name);
    }

    function renderWall(wall) {
        $("#title").text(wall.name);
        document.title = wall.name;
        
        $.each(wall.categories, function(i, category) {
            var categoryView = $(".category").realise().data("categoryId", category.id);
            categoryView.find(".name").text(category.name);
            $.each(category.entries, function(j, entry) {
                addEntry(categoryView, entry);
            });
        });
        
        hintTechnologies();
        $(".tags").keypress(function(e) {
            if (e.keyCode === $.ui.keyCode.ENTER) {
                var $el = $(this);
                addEntry($el.closest(".category"), $el.val()).addClass("new");
                $el.val("");
            }
        });
        $(".entry-list").sortable({
            items: "li:not(.new)",
            connectWith: ".entry-list",
            receive: function(event, ui) { alert("receive from: " + ui.sender.closest(".category").data("categoryId")); }
        }).disableSelection();
    }

    function hintTechnologies(names) {
        if (names) {
            tagNames = names;
        }
        $(".tags").autocomplete({
            source: tagNames
        });
    }

    $.getJSON("/walls/" + wallId + "/wall.json", renderWall);
    $.getJSON("/technologies.json", hintTechnologies);
});