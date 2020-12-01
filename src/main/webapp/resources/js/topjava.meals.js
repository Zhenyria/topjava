var curUrl = "ajax/meals/";
var ctx;

$(function () {
    ctx = {
        ajaxUrl: curUrl,
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    };
    makeEditable();
});

function filter() {
    $.ajax({
        type: "GET",
        url: curUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function resetFilter() {
    $("#filter")[0].reset();
    $.get(curUrl, updateTable);
}