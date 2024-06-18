import React, {useEffect, useState} from 'react';
import styles from'./Image.module.css';
import Index from "../index"; // 假设CSS样式文件名为ImageUpload.css

const Image = ({id,setId}) => {
    const [imageURL, setImageURL] = useState('');
    const [showActions, setShowActions] = useState(false);
    const [index,setIndex] = useState(id);

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        // 预览图片
        const reader = new FileReader();
        reader.onload = (e) => {
            setImageURL(e.target.result);
        };
        reader.readAsDataURL(file);
        upload(file)

    };

    const handleImageMouseEnter = () => {
        setShowActions(true);
    };

    const handleImageMouseLeave = () => {
        setShowActions(false);
    };

    const upload = (file) => {
        const formData = new FormData();
        formData.append('file', file);
        fetch(Index.address+'/file/upload', {
            method: 'POST',
            body: formData,
            headers: {
                'token': Index.token,
            }
        })
            .then(response => response.json())
            .then(response => {
            setIndex(response.index)
            setId(response.index)
        }).catch(error => {
            console.error(error);
        });
    }
    const del = () => {
        if(index) {
            fetch(Index.address+'/file/del/'+index,{
                headers: {
                    'token': Index.token,
                }
            })
            .then(res => {
                setImageURL('')
                setIndex(null)
                setId(null)
            });
        }
    }
    const show = async () => {
        if(index) {
            const res = await fetch(Index.address + '/file/show/' + index, {
                headers: {
                    'token': Index.token,
                }
            });
            const blob = await res.blob();
            const reader = new FileReader();
            reader.onload = (e) => {
                setImageURL(e.target.result);
            };
            reader.readAsDataURL(blob);
        }
    }
    useEffect(()=>{
        show()
    },[])
    return (
        <div className={styles.image_upload}>
            <form hidden>
                <label>
                    <input id="file" type="file" onChange={handleFileChange} />
                </label>
            </form>
            <div
                className={styles.image_preview}
                style={{ backgroundImage: `url(${imageURL})` }}
                onMouseEnter={handleImageMouseEnter}
                onMouseLeave={handleImageMouseLeave}
            >
                {showActions && (
                    <div className={styles.image_actions}>
                        <button onClick={() => window.file.click()}>上传</button>
                        {imageURL && (<button onClick={del}>删除</button>)}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Image;