package com.grp3.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.grp3.configuration.StorageProperties;
import com.grp3.io.storage.StorageException;
import com.grp3.io.storage.StorageFileNotFoundException;

/**
 ********************************************************************
 * File System implementation of IStorageService
 * 
 * Taken from: <a href="https://github.com/spring-guides/gs-uploading-files">https://github.com/spring-guides/gs-uploading-files</a>
 *
 * @author Kyle Williamson
 * @version 1.0.0
 ********************************************************************
 */
@Service
public class FileSystemStorageService implements IStorageService {
	
	private static final String EXC_INIT_STORAGE = "Could not initialize storage";
	private static final String EXC_READ_FILES_FAIL = "Failed to read stored files";
	private static final String EXC_READ_FAIL = "Could not read file: ";
	private static final String EXC_STORE_FILE_FAIL = "Failed to store file ";
	private static final String EXC_STORE_EMPTY_FILE = "Failed to store empty file ";
	

    private final Path _rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this._rootLocation = Paths.get(properties.getLocation());
    }
    
	@Override
	public void init() {
        try {
            Files.createDirectory(_rootLocation);
        } catch (IOException e) {
            throw new StorageException(EXC_INIT_STORAGE, e);
        }
		
	}

	@Override
	public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException(EXC_STORE_EMPTY_FILE + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this._rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException(EXC_STORE_FILE_FAIL + file.getOriginalFilename(), e);
        }
		
	}

	@Override
	public Stream<Path> loadAll() {
        //try {
         //   return Files.walk(this._rootLocation, 1)
          //          .filter(path -> !path.equals(this._rootLocation))
           //         .map(path -> this._rootLocation.relativize(path));
        //} catch (IOException e) {
          //  throw new StorageException(EXC_READ_FILES_FAIL, e);
        //}
	}

	@Override
	public Path load(String filename) {
        return _rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(EXC_READ_FAIL + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(EXC_READ_FAIL + filename, e);
        }
	}

	@Override
	public void deleteAll() {
        FileSystemUtils.deleteRecursively(_rootLocation.toFile());
		
	}

}
