import React, {useEffect, useRef, useState} from 'react';
import Table from "../Components/Table";
import Index from "../index";
import Image from "../Components/Image";
import styles from "./User.module.css"
function UserList() {
    const urls = "/user/pageUserList"
    const positions = {
        method: "get",
        headers: {
            "token": Index.token
        }
    }
    const [data,setData] = useState({});
    const [editStatus,setEditStatus] = useState(false);
    const [addStatus,setAddStatus] = useState(false);
    const [deleteStatus,setDeleteStatus] = useState(false);
    const [status,setStatus] = useState(1);
    const edits = (options) => {
        setData(options)
        setEditStatus(true)
        setAddStatus(false)
        setDeleteStatus(false)
    }
    const adds = (options) => {
        setData(options)
        setEditStatus(false)
        setAddStatus(true)
        setDeleteStatus(false)
    }
    const deletes = (options) => {
        setData(options)
        setEditStatus(false)
        setAddStatus(false)
        setDeleteStatus(true)
    }



    const setIndex = (index)=> {
        data.picture = index;
        setData(data)
    }
    const make = () => {
        if(data) {
            fetch(Index.address+'/user/makeUser', {
                method: 'POST',
                body: JSON.stringify(data),
                headers: {
                    'token': Index.token,
                    'Content-Type': 'application/json',
                }
            })
                .then(response => response.json())
                .then(response => {
                    setData(response)
                    setEditStatus(false)
                    setStatus(status+1)
                }).catch(error => {
                console.error(error);
            });
        }
    }
    const del = () => {
        if(data) {
            fetch(Index.address+'/user/delUser', {
                method: 'DELETE',
                body: JSON.stringify(data),
                headers: {
                    'token': Index.token,
                    'Content-Type': 'application/json',
                }
            })
                .then(response => response.json())
                .then(response => {
                    setData(null)
                    setEditStatus(false)
                    setStatus(status+1)
                }).catch(error => {
                console.error(error);
            });
        }
    }

    const inputChange = (e) => {
        const {name,value} = e.target;
        setData((datas)=>{
            return {
                ...datas,
                [name]:value
            }
        })
    }
    return (
        <div>
            {(editStatus || addStatus) && (
                <div className={styles.flush}>
                    <div className={styles.panle}>
                        <div className={styles.hidden}>
                            <label>用户ID</label>
                            <input name='userId' value={data.userId} onChange={inputChange}/>
                        </div>
                        <div className={styles.row}>
                            <div className={styles.image}>
                                <Image id={data.picture} setId={setIndex}/>
                            </div>
                            <div>
                                <div className={styles.message}>
                                    <label>姓名</label>
                                    <input name = 'name' value={data.name} onChange={inputChange}/>
                                </div>
                                <div  className={styles.message}>
                                    <label>账号</label>
                                    <input name = 'username' value={data.username} onChange={inputChange}/>
                                </div>
                                <div  className={styles.message}>
                                    <label>密码</label>
                                    <input name = 'password'  value={data.password} onChange={inputChange}/>
                                </div>
                            </div>
                        </div>
                        <div className={styles.row}>
                            <textarea style={{width:"100%",height:"100%"}} rows="5">{JSON.stringify(data,2,null)}</textarea>
                        </div>
                        <div className={styles.row}>
                            <button className={styles.confirm} onClick={()=>make()}>确定</button>
                            <button className={styles.cancel} onClick={() => setEditStatus(false)}>取消</button>
                        </div>
                    </div>
                </div>
            )}
            {deleteStatus && (
                <div className={styles.flush}>
                    <p>
                      确定删除用户{data.username}?
                    </p>
                    <div>
                        <button className={styles.confirm} onClick={()=>{del()}}>确定</button>
                        <button className={styles.cancel} onClick={() => setDeleteStatus(false)}>取消</button>
                    </div>
                </div>
            )}
            <Table urls={urls} positions={positions} edit={edits} status={status}/>
        </div>
    )
}

export default UserList;