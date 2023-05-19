import React, { useState,useEffect } from 'react';
import styles from './File.module.css';
import Index from "../index";
import ReactDOM from "react-dom/client";
import FileViewer from 'react-file-viewer';


function File() {
    const [showElement, setShowElement]  = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [fileList, setFileList] = useState([]);
    const [imagePreviewUrl, setImagePreviewUrl] = useState(null);
    const [filename, setfilename] = useState(null);

    const fileSelectedHandler = event => {
        setSelectedFile(event.target.files[0]);
        if (event.target.files && event.target.files[0]) {
            let reader = new FileReader();
            reader.onload = (e) => {
                makeUrl(event.target.files[0])
            };
            reader.readAsDataURL(event.target.files[0]);
        }
    }
    const makeUrl = (file) => {
        setfilename(file.name)
        switch (file.type) {
            case 'image/jpeg':
            case 'image/png':
                setImagePreviewUrl(URL.createObjectURL(file));
                break;
            case 'audio/mpeg':
            case 'audio/wav':
                setImagePreviewUrl('../static/ico/file_audio.png');
                break;
            case 'video/mp4':
                setImagePreviewUrl('../static/ico/file_video.png');
                break;
            case 'application/pdf':
                setImagePreviewUrl('../static/ico/file_pdf.png');
                break;
            case 'application/msword':
            case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
                setImagePreviewUrl('../static/ico/file_msword.png');
                break;
            default:
                setImagePreviewUrl('../static/ico/file_default.png');
                break;
        }
    }

    const fileDropHandler = event => {
        event.preventDefault();
        setSelectedFile(event.target.files[0]);
        // Display preview of dropped file
        if (event.dataTransfer.files[0]) {
            let reader = new FileReader();
            reader.onload = (e) => {
                makeUrl(event.target.files[0])
            };
            reader.readAsDataURL(event.dataTransfer.files[0]);
        }
    }
    const fileUploadHandler = () => {
        if (!selectedFile) return;
        const formData = new FormData();
        formData.append('file', selectedFile);
        fetch(Index.address+'/file/upload', {
            method: 'POST',
            body: formData,
            headers: {
                'token': Index.token,
            }
        }).then(response => {
            list()
            setSelectedFile('')
            setfilename('')
            setFileList([])
            window.myfile.value = null
        }).catch(error => {
            console.error(error);
        });
    };

    const fileDragHandler = event => {
        event.preventDefault();
    }
    const download = (e) => {
        e.preventDefault()
        fetch(Index.address+'/file/download/'+e.target.value,{
            headers: {
                'token': Index.token,
            }
        })
            .then(res => res.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', e.target.name);
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            });
    }
    const delfile = (e) => {
        fetch(Index.address+'/file/del/'+e,{
            headers: {
                'token': Index.token,
            }
        })
            .then(res => {
                list()
            });
    }
    const selectFile = () => {
        window.myfile.click()
    }
    const showfile = async (e) => {
        e.preventDefault();
        const res = await fetch(Index.address + '/file/show/' + e.target.id, {
            headers: {
                'token': Index.token,
            }
        });
        const blob = await res.blob();
        const type = await res.headers.get("content-type");
        await showElement.render(
            <FileViewer  fileType={type} filePath={URL.createObjectURL(blob)}/>
        )
    }
    const list = () => {
        fetch(Index.address+'/file/list',{
            method: 'Get',
            headers: {
                'token': Index.token,
            }
        })
            .then(response => response.json())
            .then(data => {
                setFileList(data);
            }).catch(error => {
            console.error(error);
        });
    }
    useEffect(() => {
        list()
        setShowElement(ReactDOM.createRoot(document.getElementById('showfile')))
    }, []);

    return (
        <div className={styles.file_upload_container} onDrop={fileDropHandler} onDragOver={fileDragHandler}>
            <div className={styles.file_upload}>
                {selectedFile && (
                    <div className={styles.file_preview} onClick={selectFile}>
                        <img src={imagePreviewUrl} alt="Preview" className={styles.preview_image}/>
                        <span>{filename}</span>
                    </div>
                )}
                {!selectedFile && <div className={styles.file_preview_placeholder} onClick={selectFile}></div>}
                <input id = "myfile" type="file" className={styles.file_input} onChange={fileSelectedHandler} />
                <button className={styles.upload_button} onClick={fileUploadHandler}>Upload</button>
                <div id = "showfile" className={styles.file_show}>
                </div>
            </div>
            <div className={styles.file_list}>
                {fileList.length === 0 &&
                <div className={styles.file_list_empty}>No files uploaded yet.</div>
                }
                {fileList.length>0 && fileList.map((file, index) => (
                    <div className={styles.file_list_item} key={index}>
                        <a id={file.index} onClick={showfile}  title={new Date(file.time).toLocaleString()}  href="#" target="_blank" rel="noopener noreferrer" className={styles.file_link}>{file.name}</a>
                        <span className={styles.file_size}>{(file.size / 1000).toFixed(1)} KB</span>
                        <button className={styles.file_delete_button} name={file.name} value={file.index} onClick={download}>Download</button>
                        <button className={styles.file_delete_button} onClick={() => delfile(file.index)}>Delete</button>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default File;