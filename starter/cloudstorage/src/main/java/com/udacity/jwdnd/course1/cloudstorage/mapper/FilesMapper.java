package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FilesMapper {

    @Select("SELECT * FROM FILES WHERE userid = #{userid}")
    List<Files> getFiles(Integer userid);

    @Select("SELECT * FROM FILES WHERE filename = #{filename} AND userid = #{userid}")
    List<Files> CheckFile(String filename, Integer userid);

    @Select("SELECT * FROM FILES WHERE fileId = #{fileId} AND userid = #{userid}")
    List<Files> getFile(Integer fileId, Integer userid);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES (#{filename}, #{contenttype},#{filesize},  #{userid}, #{filedata})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    Integer addFile(Files files);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId} AND userid=#{userid}")
    boolean removeFile(Integer fileId, Integer userid);
}
