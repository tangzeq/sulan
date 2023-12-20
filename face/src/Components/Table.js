import {useSortBy, useTable,usePagination} from 'react-table';
import React, {useMemo, useState} from 'react';
import _ from './Table.module.css';


function Table({columns,data}) {
    // const [state,setState] = useState([{ pageIndex, pageSize }]);
    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
        page,
        canPreviousPage,
        canNextPage,
        pageOptions,
        pageCount,
        gotoPage,
        nextPage,
        previousPage,
        setPageSize,
        state:{ pageIndex, pageSize }
    } = useTable(
        {
            columns,
            data,
        },
        useSortBy,
        usePagination,
    )
    return (
        <>
        <table {...getTableProps()}>
            <thead>
            {headerGroups.map((headerGroup) => (
                <tr {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map((column) => (
                        <th {...column.getHeaderProps(column.getSortByToggleProps())}>{column.render('Header')}
                            <span>
                                    <span>
                                        {column.sorted ? (column.isSortedDesc ? ' 🔽' : ' 🔼') : ''}
                                    </span>
                            </span>
                        </th>
                    ))}
                </tr>
            ))}
            </thead>
            <tbody {...getTableBodyProps()}>
            {page.map((row, i) => {
                prepareRow(row)
                return (
                    <tr {...row.getRowProps()}>
                        {row.cells.map((cell) => {
                            return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                        })}
                    </tr>
                )
            })}
            </tbody>
            <tfoot>
            <div className="pagination">
                <button onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
                    {'<<'}
                </button>{' '}
                <button onClick={() => previousPage()} disabled={!canPreviousPage}>
                    {'<'}
                </button>{' '}
                <button onClick={() => nextPage()} disabled={!canNextPage}>
                    {'>'}
                </button>{' '}
                <button onClick={() => gotoPage(pageCount - 1)} disabled={!canNextPage}>
                    {'>>'}
                </button>{' '}
                <span>
          Page{' '}
                    <strong>
            {pageIndex + 1} of {pageOptions.length}
          </strong>{' '}
        </span>
                <span>
          | Go to page:{' '}
                    <input
                        type="number"
                        defaultValue={pageIndex + 1}
                        onChange={e => {
                            const page = e.target.value ? Number(e.target.value) - 1 : 0
                            gotoPage(page)
                        }}
                        style={{ width: '100px' }}
                    />
        </span> <select
                value={pageSize}
                onChange={e => {
                    setPageSize(Number(e.target.value))
                }}
            >
                {[10, 20, 30, 40, 50].map(pageSize => (
                    <option key={pageSize} value={pageSize}>
                        Show {pageSize}
                    </option>
                ))}
            </select>
            </div>
            </tfoot>
        </table>

        </>
    )
}

export default Table;