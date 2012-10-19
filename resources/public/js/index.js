$(document).ready(function() {
    $.getJSON("/walls.json", function(wallListJson) {
        $.each(wallListJson, function(index, wallJson) {
            var item = $("<li></li>").append($("<a></a>").attr("href", "/walls/" + wallJson.id).text(wallJson.name));
            $('#wall-list ul').append(item);
        });
    });
});