import {
    useTable,
    useFilters,
    useGlobalFilter,
    usePagination,
    useRowSelect,
} from 'react-table'
import React, {useEffect, useState} from 'react';
import styles from "./Table.module.css";
import Index from "../index";

function Table({urls, positions,edit,add,del,status}){
    const [data, setData] = useState([])
    const [columns, setColumns] = useState([])
    const defaultColumn = {
        Filter: ({column: {filterValue, setFilter, Header}}) => (
            <div className={styles.filter}>
                <input
                    value={filterValue || ''}
                    onChange={e => {
                        setFilter(e.target.value || undefined) // Set undefined to remove the filter entirely
                    }}
                    placeholder={"请输入" + Header}
                />
            </div>
        ),
    }

    const IndeterminateCheckbox = React.forwardRef(
        ({indeterminate, ...rest}, ref) => {
            const defaultRef = React.useRef()
            const resolvedRef = ref || defaultRef

            React.useEffect(() => {
                resolvedRef.current.indeterminate = indeterminate
            }, [resolvedRef, indeterminate])

            return <input type="checkbox" ref={resolvedRef} {...rest} />
        }
    )

    const withSelect = (pushs) => {
        const theSelect = [{
            id: 'selection',
            // The header can use the table's getToggleAllRowsSelectedProps method
            // to render a checkbox
            Header: ({getToggleAllRowsSelectedProps}) => (
                <div className={styles.selector}>
                    <label>
                        <IndeterminateCheckbox {...getToggleAllRowsSelectedProps()} />{' '}
                    </label>
                </div>
            ),
            // The cell can use the individual row's getToggleRowSelectedProps method
            // to the render a checkbox
            Cell: ({row}) => (
                <div className={styles.selector}>
                    <label>
                        <IndeterminateCheckbox {...row.getToggleRowSelectedProps()} />{' '}
                    </label>
                </div>
            ),
        },
            {
                id: 'selectedStatus',
                Cell: ({row: {id}}) => (
                    <div>
                        {Math.round(id) + 1}
                        {/*{row.isSelected ? 'Selected' : 'Not Selected'}*/}
                    </div>
                ),
            }
        ];
        theSelect.push(...pushs);
        return theSelect;

    }

    const makeData = () => {
        fetch(Index.address + urls, positions).then(res => res.text())
            .then(res => {
                res = JSON.parse(res)
                const headers = withSelect(res.headers);
                columns.push(...headers)
                setColumns([...headers])
                data.push(...res.data)
                setData([...res.data])
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            })
    }
    const wickets = (options) => {
        const values = rows[Object.keys(state.selectedRowIds)[0]].values;
        options(values);
    }
    useEffect(() => {
        makeData()
        return () => {
        }
    }, [status])
    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
        visibleColumns,
        state: {pageIndex, pageSize},
        state,
        setGlobalFilter,
        page,
        canPreviousPage,
        canNextPage,
        pageOptions,
        pageCount,
        gotoPage,
        nextPage,
        previousPage,
        setPageSize,
    } = useTable(
        {
            columns,
            data,
            initialState: {pageIndex: 0},
            defaultColumn,
        },
        useFilters,
        useGlobalFilter,
        usePagination,
        useRowSelect,
    )
    return (
        <div>
            <div className={styles.bottom_list} {...getTableProps()}>
      {/*          <p>*/}
      {/*              Selected Rows:{' '}*/}
      {/*              <span data-testid="selected-count">*/}
      {/*    {Object.keys(state.selectedRowIds).length}*/}
      {/*  </span>*/}
      {/*          </p>*/}

      {/*      {Object.keys(state.selectedRowIds).length>0?Object.keys(state.selectedRowIds).map(i =>(*/}
      {/*          <pre>*/}
      {/*  <code>*/}
      {/*          {JSON.stringify(rows[i].values,null,2)}*/}
      {/*      </code>*/}
      {/*</pre>*/}
      {/*      )):"1"}*/}

                <div className={styles.filter}>
                    <div>
                        <button className={styles.button} onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
                            {'<<'}
                        </button>
                        {' '}
                        <button className={styles.button} onClick={() => previousPage()} disabled={!canPreviousPage}>
                            {'<'}
                        </button>
                        {' '}
                        <button className={styles.button} onClick={() => nextPage()} disabled={!canNextPage}>
                            {'>'}
                        </button>
                        {' '}
                        <button className={styles.button} onClick={() => gotoPage(pageCount - 1)}
                                disabled={!canNextPage}>
                            {'>>'}
                        </button>
                        {' '}
                        <span className={styles.button}>
          {' '}
                            <span className={styles.button}>
            {pageIndex + 1} / {pageOptions.length}
          </span>{' '}
        </span>
                        <span className={styles.button}>
          | 跳转:{' '}
                            <input
                                type="number"
                                defaultValue={pageIndex + 1}
                                onChange={e => {
                                    const page = e.target.value ? Number(e.target.value) - 1 : 0
                                    gotoPage(page)
                                }}
                                className={styles.button}
                            />
        </span>{' '}
                        <select className={styles.button}
                                value={pageSize}
                                onChange={e => {
                                    setPageSize(Number(e.target.value))
                                }}
                                data-testid="page-size-select"
                        >
                            {[10, 20, 30, 40, 50].map(pageSize => (
                                <option key={pageSize} value={pageSize}>
                                    {pageSize}
                                </option>
                            ))}
                        </select>
                        <button className={styles.button} onClick={makeData}>刷新</button>
                        <input className={styles.globalfilter}
                               value={state.globalFilter || ''}
                               onChange={e => {
                                   setGlobalFilter(e.target.value || undefined) // Set undefined to remove the filter entirely
                               }}
                        />
                        <button className={styles.button} style={{display:add ?'contents':'none'}} onClick={()=>wickets(add)}>
                            {'新增'}
                        </button>
                        <button className={styles.button} style={{display:edit ?'contents':'none'}} onClick={()=>wickets(edit)}>
                            {'修改'}
                        </button>
                        <button className={styles.button} style={{display:del ?'contents':'none'}} onClick={()=>wickets(del)}>
                            {'删除'}
                        </button>
                    </div>
                </div>
                <div className={styles.header}>
                    {headerGroups.map(headerGroup => (
                        <div className={styles.row} {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map(column => (
                                <div
                                    className={styles.t_b_1} {...column.getHeaderProps()}>
                                    <div className={styles.cell}>{column.render('Header')}</div>
                                    {column.canFilter ? column.render('Filter') : null}</div>
                            ))}
                        </div>
                    ))}
                </div>
                <div className={styles.body} {...getTableBodyProps()}>
                    {page.map(
                        (row, i) => prepareRow(row) || (
                            <div className={styles.row} {...row.getRowProps()}>
                                {row.cells.map(cell => {
                                    return (
                                        <div
                                            className={styles.t_b_2} {...cell.getCellProps()}>{cell.render('Cell')}</div>
                                    )
                                })}
                            </div>
                        )
                    )}
                </div>
            </div>
        </div>
    )
}

export default Table;