package one.dfy.bily.api.common.service;

import one.dfy.bily.api.common.dto.FileInfoEntity;
import one.dfy.bily.api.common.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;

    public void saveFile(FileInfoEntity fileInfoEntity) {
        fileMapper.insertFile(fileInfoEntity);
    }

    public void saveBusinessCardFile(FileInfoEntity fileInfoEntity) {
        fileMapper.insertBusinessCardFile(fileInfoEntity);
    }

    public List<FileInfoEntity> getFileList(Long contentId) {
        return fileMapper.selectAllFiles(contentId);
    }

    public FileInfoEntity getFileById(Long id) {
        return fileMapper.selectFileById(id);
    }

    public void deleteFile(Long id) {
        fileMapper.deleteFile(id);
    }

    public void updateFile(FileInfoEntity fileInfoEntity) {
        fileMapper.updateFile(fileInfoEntity);
    }
}
