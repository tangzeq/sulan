import React, {useEffect, useState} from 'react';
import styles from './Person.module.css';
import Index from "../index";
function Person() {
    const [user, setUser] = useState({})
    const [name, setName] = useState('')
    const handleName = (e) => {
        e.preventDefault();
        setName(e.target.value)
    }
    const changeName = (e) => {
        if(e.target.value) fetch(Index.address + "/user/putName/"+e.target.value, {
            method: "Put",
            headers: {
                "Content-Type": "application/json",
                "token": Index.token,
            }
        })
            .then(res => res.json())
            .then(res => {
            })
            .catch(err => {
                setName(user.name)
            })
    }
    const getUser = () => {
        fetch(Index.address + "/user/getUser", {
            method: "Get",
            headers: {
                "Content-Type": "application/json",
                "token": Index.token,
            }
        })
            .then(res => res.json())
            .then(res => {
                if(!res.picture) res.picture = "../static/ico/customer.ico"
                setName(res.name)
                setUser(res)
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            })
    }
    useEffect(() => {
        getUser()
        return;
    },[]);
    return (
        <div className={styles.container_person}>
            <header>
                <div className={styles.img}>
                    <img alt='' src={user.picture}/>
                </div>
            </header>
            <main>
                <section>
                    <ul>
                        <li>
                            <h3>用户信息</h3>
                            <ul className={styles.messages}>
                                <li><input className={styles.val} value={name} onBlur={changeName} onChange={handleName} /><span className={styles.lable}>姓名</span></li>
                                <li>{user.username}<span className={styles.lable}>账号</span></li>
                                <li>{user.userId}<span className={styles.lable}>标识</span></li>
                            </ul>
                        </li>
                    </ul>
                </section>
            </main>
        </div>
    );
}

export default Person;