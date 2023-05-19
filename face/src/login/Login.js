import React, {useEffect, useState} from "react";
import styles from "./Login.module.css"
import Index from "../index";
import ReactDOM from "react-dom/client";
import Platform from "../platform/Platform";
const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [address, setAddress] = useState("");
    const handleUsernameChange = (e) => {
        setUsername(e.target.value);
    };
    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    };
    const handleAddressChange = (e) => {
        setAddress(e.target.value);
    };
    const handleSubmit = (e) => {
        e.preventDefault();
        fetch(address+"/login/login", {
            method: "Get",
            headers: {
                "Content-Type": "application/json",
                "username": username,
                "password": password,
            }
        })
            .then(response =>response.json().then(json =>({
                json,
                token:response.headers.get("expires")
            })))
            .then(result => {
                if (result && result && !result.json.status) onlogin(result);
                else throw "登录失败";
            })
            .catch((error) => {
                console.error(error);
                window.location.reload()
            });
    };
    const onlogin = (res) => {
        Index.root = ReactDOM.createRoot(document.getElementById('index'));
        Index.address = address;
        Index.userId = res.json.userId;
        Index.userName = res.json.name;
        Index.token = res.token;
        platform()
    }
    const platform = () => {
        Index.root.render(
            <React.StrictMode>
                <Platform />
            </React.StrictMode>
        );
    }
    useEffect(() => {
        setAddress(window.location.origin)
        return ()=> setAddress(window.location.origin)
    },[])
    return (
        <div className={styles.index}>
            <Index />
            <div id="index">
                <form className={styles.container} onSubmit={handleSubmit}>
                    <div className={styles.panel}>
                        <div className={styles.inputContainer}>
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={handleUsernameChange}
                                className={styles.input}
                                placeholder="请输入账号"
                                required
                            />
                        </div>
                        <div className={styles.inputContainer}>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={handlePasswordChange}
                                className={styles.input}
                                placeholder="请输入密码"
                                required
                            />
                        </div>
                        <div className={styles.inputContainer}>
                            <input
                                type="text"
                                id="address"
                                value={address}
                                onChange={handleAddressChange}
                                className={styles.input}
                                placeholder="服务器（如127.0.0.1:8080）"
                                required
                            />
                        </div>
                        <button type="submit" className={styles.button}>
                            Login
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
export default Login;