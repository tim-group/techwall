$(document).ready(function() {

    function addWall(wall) {
        var $wallEntryView = $(".wall-entry").realise();
        $wallEntryView.find(".wall-link").attr("href", "/wall.html?id=" + wall.id).text(wall.name);
    }

    function createNewWall(name) {
        $.post("/wall", {"name": name}, addWall, "json");
    }

    $.getJSON("/walls", function(wallListJson) {
        $.each(wallListJson, function(index, wallJson) {
            addWall(wallJson);
        });
    });
    $(".add-wall").keypress(function(e) {
        if (e.keyCode === $.ui.keyCode.ENTER) {
            createNewWall($(this).val());
            $(this).val("");
        }
    });
});