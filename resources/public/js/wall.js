$(document).ready(function() {
    var technologyNames = [],
        colourmap = [],
        wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;

    if (!wallId) {
        window.location.replace("/");
        return;
    }

    function removeDuplicatesOf($entryView) {
        var entryId = $entryView.data("entryId"),
            entryView = $entryView.get(0);

        $("li.entry").filter(function() {
            return this !== entryView && $(this).data("entryId") === entryId;
        }).remove();
    }

    function persist($categoryView, $entryView) {
        var categoryId = $categoryView.data("categoryId"),
            entryId = $entryView.data("entryId"),
            entryName = $entryView.text();

        $.post("/wall/" + wallId + "/category/" + categoryId + "/entry", {"techId": entryId, "techName": entryName}, function(entry) {
            updateEntry($entryView, entry).removeClass("new");
            removeDuplicatesOf($entryView);
        }, "json");
    }

    function handleEntryEdit() {
        $("#edit-tech").entryeditor("show", $(this));
    }

    function applyColour($entryView) {
        var techtypeId = $entryView.data("techtypeId");
        if (techtypeId && colourmap[techtypeId]) {
            $entryView.css("background-color", colourmap[techtypeId]);
        }
        return $entryView;
    }

    function updateEntry($entryView, entry) {
        return applyColour($entryView.text(entry.name)
                                     .data("entryId", entry.id)
                                     .data("techtypeId", entry.techtypeid)
                                     .dblclick(handleEntryEdit));
    }

    function createEntry($categoryView, entry) {
        return updateEntry($categoryView.find(".entry").realise(), entry);
    }

    function renderWall(wall) {
        $("#title").text(wall.name);
        document.title = wall.name;
        
        $.each(wall.categories, function(i, category) {
            var categoryView = $(".category").realise().data("categoryId", category.id);
            categoryView.find(".name").text(category.name);
            $.each(category.entries, function(j, entry) {
                createEntry(categoryView, entry);
            });
        });
        
        hintTechnologies();
        applyColours();
        
        $(".add-entry").keypress(function(e) {
            if (e.keyCode === $.ui.keyCode.ENTER) {
                var $el = $(this),
                    $categoryView = $el.closest(".category");
                if ($.trim($el.val())) {
                    persist($categoryView, createEntry($categoryView, {"name": $.trim($el.val())}).addClass("new"));
                }
                $el.val("");
            }
        });
        
        function entryReceived(event, ui) {
            persist($(this).closest(".category"), ui.item);
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

    function applyColours(techtypes) {
        if (techtypes) {
            colourmap = [];
            $.each(techtypes, function(i, techtype) {
                colourmap[techtype.id] = (techtype.colour);
            });
        }
        $("li.entry").each(function(i, entryView) {
            applyColour($(entryView));
        });
    }
    
    $("#edit-tech").entryeditor();
    
    $.getJSON("/wall/" + wallId, renderWall);
    $.getJSON("/technologies", hintTechnologies);
    $.getJSON("/techtypes", applyColours);
});