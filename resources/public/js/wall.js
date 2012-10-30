$(document).ready(function() {
    var technologyNames = [],
        wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;

    if (!wallId) {
        window.location.replace("/");
    }

    function handleMove(fromId, toId, entryId, entryName) {
        console.log("entry '" + entryName + "' (id: " + entryId + ") moved from: " + fromId + " to: " + toId);
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
                addEntry(categoryView, entry.name).data("entryId", entry.id);
            });
        });
        
        hintTechnologies();
        $(".add-entry").keypress(function(e) {
            if (e.keyCode === $.ui.keyCode.ENTER) {
                var $el = $(this);
                addEntry($el.closest(".category"), $el.val()).addClass("new");
                $el.val("");
            }
        });
        
        function entryReceived(event, ui) {
            var fromId = ui.sender.closest(".category").data("categoryId"),
                toId = $(this).closest(".category").data("categoryId"),
                entryId = ui.item.data("entryId"),
                entryName = ui.item.text();
            handleMove(fromId, toId, entryId, entryName);
        }
        
        $(".entry-list").sortable({
            items: "li:not(.new)",
            connectWith: ".entry-list",
            receive: entryReceived
        }).disableSelection();
    }

    function hintTechnologies(technologies) {
        if (technologies) {
            technologyNames = [];
            $.each(technologies, function(i, technology) {
                technologyNames.push(technology.name);
            });
        }
        $(".add-entry").autocomplete({
            source: technologyNames
        });
    }

    $.getJSON("/walls/" + wallId + "/wall.json", renderWall);
    $.getJSON("/technologies.json", hintTechnologies);
});