var ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "admin/users/",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
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
                    "asc"
                ]
            ]
        })
    };
    makeEditable(function () {
        $.get(ctx.ajaxUrl, updateTableByData)
    });
});

function setEnabled(checkbox, id) {
    var enabled = checkbox.is(":checked")
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + id,
        data: {enabled: enabled}
    }).done(function () {
        checkbox.closest("tr").attr("data-userEnabled", enabled);
        successNoty(enabled ? "User enabled" : "User disabled");
    }).fail(function () {
        $(checkbox).prop("checked", !enabled);
    });
}