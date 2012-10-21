$(document).ready(function() {
    var wallId = decodeURIComponent((new RegExp("[?|&]id=([\\d]+)").exec(location.search)||[,""])[1])||null;
    if (wallId) {
        $.getJSON("/walls/" + wallId + "/wall.json", function(wallJson) {
        });
    }
    
    $(".connectedSortable").sortable({
        connectWith: ".connectedSortable"
    }).disableSelection();
});