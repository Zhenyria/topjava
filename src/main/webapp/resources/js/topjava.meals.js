var ctx;

$(function () {
    ctx = {
        ajaxUrl: "ajax/meals/",
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
    makeEditable(updateFilteredTable);
});

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function resetFilter() {
    $("#filter")[0].reset();
    $.get(ctx.ajaxUrl, updateFilteredTable());
}