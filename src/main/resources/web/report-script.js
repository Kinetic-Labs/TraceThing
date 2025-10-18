const filterTable = () => {
    const input = document.getElementById('filter-input');
    const filter = input.value.toUpperCase();
    const table = document.getElementById('report-table');
    const tr = table.getElementsByTagName('tr');

    for(let i = 1; i < tr.length; i++) {
        const td = tr[i].getElementsByTagName('td')[0];

        if(!td)
            return;

        const txtValue = td.textContent || td.innerText;

        if(txtValue.toUpperCase().indexOf(filter) > -1) {
            tr[i].style.display = '';
        } else {
            tr[i].style.display = 'none';
        }
    }
}
