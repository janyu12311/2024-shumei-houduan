package com.laterya.hearing.util;

import com.alibaba.druid.util.StringUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int FILE_NAME_MAX = 100;

    private static String DEFAULT_BASE_FILE="/home/zzp/images/" ;
   /* private static String DEFAULT_BASE_FILE="D:/test/" ;*/

    /*private static String accessPath="/uploadimg/";*/
    private static String accessPath="/uploadimg/";
    /**/


    /**
     * 按照默认的配置上传文件
     *
     * @param file 文件
     * @return 文件名
     * @throws IOException
     */
    public static final String upload(MultipartFile file) throws IOException {

        try {
            return upload(FileUploadUtils.DEFAULT_BASE_FILE, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws IOException                    比如读写文件出错时
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws Exception {

        //合法性校验
        assertAllowed(file, allowedExtension);

        String fileName = encodingFileName(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc);
        String path="http://101.34.85.209:5251"+accessPath+fileName;

        return path;
    }

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        System.err.println("uploadDir:"+uploadDir);
        File desc = new File(uploadDir + File.separator + fileName);
        desc.setWritable(true,false);
        System.out.println("文件可以写吗？："+desc.canWrite());
        System.out.println("文件可以执行吗？："+desc.canExecute());
        System.out.println("separator:"+File.separator);
        System.err.println("desc:"+desc);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        System.err.println("文件路径："+desc.getAbsolutePath());
        return desc;
    }


    /**
     * 对文件名特殊处理一下
     *
     * @param file 文件
     * @return
     */
    private static String encodingFileName(MultipartFile file) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String datePath = simpleDateFormat.format(new Date());
        return datePath + "-" + UUID.randomUUID().toString() + "." + getExtension(file);
    }


    /**
     * 文件合法性校验
     *
     * @param file 上传的文件
     * @return
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) throws Exception {
        if (file.getOriginalFilename() != null) {
            int fileNamelength = file.getOriginalFilename().length();
            if (fileNamelength > FILE_NAME_MAX) {
                throw new Exception("文件名过长");
            }
        }
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new Exception("文件过大");
        }
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            throw new Exception("请上传指定类型的文件！");
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = null;
        if (fileName == null) {
            return null;
        } else {
            int index = indexOfExtension(fileName);
            extension = index == -1 ? "" : fileName.substring(index + 1);
        }

        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }


    public void setDEFAULT_BASE_FILE(String DEFAULT_BASE_FILE) {
        FileUploadUtils.DEFAULT_BASE_FILE = DEFAULT_BASE_FILE;
    }

    public String getDEFAULT_BASE_FILE() {
        return DEFAULT_BASE_FILE;
    }
}

