$(document).ready(function() {
    var technologyNames = [],
        wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;

    if (!wallId) {
        window.location.replace("/");
        return;
    }

    function postJSON(url, data, successCallback) {
        $.post(url, data, successCallback, "json");
    }

    function persistMove(fromCategoryId, toCategoryId, entryId, entryName) {
        postJSON("/walls/" + wallId + "/categories/" + toCategoryId + "/entries", {"id": entryId, "name": entryName}, function() {
            console.log("entry '" + entryName + "' (id: " + entryId + ") moved from: " + fromCategoryId + " to: " + toCategoryId);
        });
    }

    function persistAdd(toCategoryId, entryName) {
        postJSON("/walls/" + wallId + "/categories/" + toCategoryId + "/entries", {"name": entryName}, function() {
            console.log("entry '" + entryName + "' added to: " + toCategoryId);
        });
    }

    function renderEntry($categoryView, name) {
        return $categoryView.find(".entry").realise().text(name);
    }

    function renderWall(wall) {
        $("#title").text(wall.name);
        document.title = wall.name;
        
        $.each(wall.categories, function(i, category) {
            var categoryView = $(".category").realise().data("categoryId", category.id);
            categoryView.find(".name").text(category.name);
            $.each(category.entries, function(j, entry) {
                renderEntry(categoryView, entry.name).data("entryId", entry.id);
            });
        });
        
        hintTechnologies();
        $(".add-entry").keypress(function(e) {
            if (e.keyCode === $.ui.keyCode.ENTER) {
                var $el = $(this);
                renderEntry($el.closest(".category"), $el.val()).addClass("new");
                persistAdd($el.closest(".category").data("categoryId"), $el.val());
                $el.val("");
            }
        });
        
        function entryReceived(event, ui) {
            var fromId = ui.sender.closest(".category").data("categoryId"),
                toId = $(this).closest(".category").data("categoryId"),
                entryId = ui.item.data("entryId"),
                entryName = ui.item.text();
            persistMove(fromId, toId, entryId, entryName);
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