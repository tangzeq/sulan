import React, {useMemo, useState} from 'react';
import styles from './User.module.css';
import Table from '../Components/Table';


function User() {
    const columns = useMemo(() => [
        {
            Header: '个人信息',
            sorted:false,
            columns:[
                {
                    Header: '姓名',
                    accessor: 'name',
                    sorted:true,
                }, {
                    Header: '性别',
                    accessor: 'sex',
                    sorted:true,
                }, {
                    Header: '年龄',
                    accessor: 'age',
                    sorted:true,
                },
            ]
        }
    ], []);
    const [data, setData] = useState([]);
    const [req, setReq] = useState({});
    const search = (e) => {
        e.preventDefault();
        console.info(req)
        // while (data.length) data.pop()
        data.push({name: "tang", sex: "man", age: "17",});
        data.push({name: "ze", sex: "man", age: "18",});
        data.push({name: "qi", sex: "man", age: "19",});
        setData([...data]);
    }
    const name = (e) => {
        e.preventDefault();
        req.name = e.target.value
        setReq(req)
    }
    const sex = (e) => {
        e.preventDefault();
        req.sex = e.target.value
        setReq(req)
    }
    return (
        <div className={styles.container_user}>
            <div className={styles.search}>
                <label>名称</label><input value={req.name} onChange={name}/>
                <label>性别</label><input value={req.sex} onChange={sex}/>
                <button onClick={search}>搜索</button>
            </div>
            <div className={styles.list}>
                <Table columns={columns} data={data}></Table>
            </div>
        </div>
    )
}

export default User;