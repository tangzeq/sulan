import React, {useEffect, useRef, useState} from 'react';
import styles from './Server.module.css';
import Index from "../index";

function Server() {
    const [server, setServer] = useState({});
    const [serverhost, setServerhost] = useState("");
    const [serverport, setServerport] = useState("");
    let [serverstart, setServerstart] = useState(0);
    const [customerhost, setCustomerhost] = useState("");
    const [customerport, setCustomerport] = useState("");
    let [customerstart, setCustomerstart] = useState(0);
    Server.setServer = setServer;
    const handleserverhost = (e) => setServerhost(e.target.value)
    const handleserverport = (e) => setServerport(e.target.value)
    const handlecustomerhost = (e) => setCustomerhost(e.target.value)
    const handlecustomerport = (e) => setCustomerport(e.target.value)
    const change = useRef(null)
    const changeServer = () => {
        if(change.current) {
            window.clearTimeout(change.current)
            change.current = null
        }
        fetch(Index.address + "/net/getServerInfo", {
            method: "Get",
            headers: {
                "Content-Type": "application/json",
                "token": Index.token,
            }
        })
            .then(res => res.json())
            .then(res => {
                if (res.serverhost && serverstart < 1) {
                    serverstart++
                    setServerstart(serverstart)
                    setServerhost(res.serverhost)
                    setServerport(res.serverport)
                }
                if (JSON.stringify(res.remotes) !== '[]' && (res.remotes)[0].host && customerstart < 1) {
                    customerstart++
                    setCustomerstart(customerstart)
                    setCustomerhost((res.remotes)[0].host)
                    setCustomerport((res.remotes)[0].port)
                }
                setServer(res)
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            }).finally(() => {
            change.current = window.setTimeout(changeServer,1000)
        });
    }
    const startServer = () => {
        setServerstart(1)
        fetch(Index.address + "/login/server/" + serverhost + "/" + serverport, {
            method: "Get",
            headers: {
                "token": Index.token
            }
        }).then(res => res.text())
            .then(res => {
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            })
    }
    const startcustomer = () => {
        setCustomerstart(1)
        fetch(Index.address + "/login/connect/" + customerhost + "/" + customerport, {
            method: "Get",
            headers: {
                "token": Index.token
            }
        }).then(res => res.text())
            .then(res => {
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            })
    }
    const closeChannle = (e) => {
        fetch(Index.address + "/net/closeCustomer/" + e.target.id, {
            method: "Put",
            headers: {
                "token": Index.token
            }
        }).then(res => res.text())
            .then(res => {
            })
            .catch(err => {
                console.error(err)
                window.location.reload()
            })
    }
    const platform = () => {
    }

    useEffect(() => {
        changeServer()
        return () => {
            close()
        };
    }, [])
    const close = () => {
        if(change.current) {
            window.clearTimeout(change.current)
            change.current = null
        }
    }
    return (
        <div className={styles.container_left}>
            <header>
                <div className={styles.img}>
                    <img alt="" src="../static/ico/author.ico"/>
                </div>
                <div className={styles.notices}>
                    <ul>
                        <li>
                            <div id="server" className={styles.notice}>
                                <span className={styles.hostlable}>服务地址:</span>
                                <input value={serverhost} onChange={handleserverhost} className={styles.host}/>
                                <span className={styles.portlable}>端口:</span>
                                <input value={serverport} onChange={handleserverport} className={styles.port}/>
                                <button onClick={startServer} className={styles.start}>
                                    <span className={styles.startlable}>{serverstart > 0 ? "变更" : "开启"}</span>
                                </button>
                            </div>
                        </li>
                        <li>
                            <div id="customer" className={styles.notice}>
                                <span className={styles.hostlable}>通讯地址:</span>
                                <input value={customerhost} onChange={handlecustomerhost} className={styles.host}/>
                                <span className={styles.portlable}>端口:</span>
                                <input value={customerport} onChange={handlecustomerport} className={styles.port}/>
                                <button onClick={startcustomer} className={styles.start} >
                                    <span className={styles.startlable}>{customerstart > 0 ? "变更" : "开启"}</span>
                                </button>
                            </div>
                        </li>
                        <li>
                            <div className={styles.notice}>
                                <span className={styles.hostlable}>网路地址:</span>
                                <span>{Index.address}</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </header>
            <main>
                <section>
                    <ul>

                        <li>
                            <h3>服务机节点信息</h3>
                            <ul className={styles.messages}>
                                {server.nodes && server.nodes.map((node, index) => (
                                    <li id={'nodes' + index}>{node.type} : {node.host}:{node.port}</li>
                                ))}
                            </ul>
                        </li>
                        <li>
                            <h3>客户机节点信息</h3>
                            <ul className={styles.messages}>
                                {server.channles && server.channles.map((channle, index) => (
                                    <li id={'channles' + index}>{channle.type} : {channle.host}:{channle.port}
                                        <button id={channle.type} onClick={closeChannle}
                                                className={styles.start}>断开
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </li>
                    </ul>
                </section>
            </main>
        </div>
    );
}

export default Server;