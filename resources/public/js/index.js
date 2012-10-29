$(document).ready(function() {
    $.getJSON("/walls.json", function(wallListJson) {
        $.each(wallListJson, function(index, wallJson) {
            var wallEntryView = $(".wall-entry").realise();
            wallEntryView.find(".wall-link").attr("href", "/wall.html?id=" + wallJson.id).text(wallJson.name);
        });
    });
});