package fr.microtec.geo2.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Controller for file manager component.
 */
@RestController
@RequestMapping("/file-manager")
public class FileManagerController {

	private final Maddog2FileSystemService fileSystemService;
	private final Map<String, Path> tmpUploadFiles;

	public FileManagerController(Maddog2FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
		this.tmpUploadFiles = new HashMap<>();
	}

	/**
	 * Execute generic command.
	 */
	@PostMapping("/execute/{command}")
	public Object execute(
			@PathVariable FS_COMMAND command,
			@RequestBody Map<String, String> body
	) {
		Object result = null;

		String basePath = this.fileSystemService.buildKeyIdPath(body.get("key"), body.get("id"));
		String path = basePath + body.get("path");

		switch (command) {
			case LIST:
				result = this.fileSystemService.toFileSystemItem(this.fileSystemService.list(path));
				break;
			case RENAME:
				this.fileSystemService.rename(path, body.get("name"));
				break;
			case COPY:
				this.fileSystemService.copy(path, basePath + body.get("dest"));
				break;
			case DELETE:
				this.fileSystemService.delete(path);
				break;
			case MOVE:
				this.fileSystemService.move(path, body.get("dest"));
				break;
			case CREATE_DIR:
				this.fileSystemService.createDirectory(path, body.get("name"));
				break;
			case ABORT_UPLOAD:
				Path tmpFile = this.tmpUploadFiles.remove(body.get("uploadId"));
				this.fileSystemService.delete(tmpFile);
				break;
		}

		return result;
	}

	/**
	 * Upload command.
	 */
	@PostMapping("/execute/upload")
	public void upload(@RequestPart(value = "chunk") MultipartFile chunk, @RequestParam("args") String args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		MultipartArgs arguments = mapper.readValue(args, MultipartArgs.class);

		boolean firstChunk = arguments.chunkData.index == 0;
		boolean lastChunk = arguments.chunkData.index == arguments.chunkData.totalCount - 1;

		if (firstChunk) {
			this.tmpUploadFiles.put(arguments.chunkData.uploadId, this.fileSystemService.createTempFile());
		}

		// Write chunk to temp file
		Path tmpFile = this.tmpUploadFiles.get(arguments.chunkData.uploadId);
		this.fileSystemService.save(tmpFile, chunk.getBytes(), StandardOpenOption.APPEND);

		if (lastChunk) {
			String path = this.fileSystemService.buildKeyIdPath(arguments.key, arguments.id) + arguments.dest;

			this.fileSystemService.move(tmpFile, path, arguments.name);
			this.tmpUploadFiles.remove(arguments.chunkData.uploadId);
		}
	}

	/**
	 * Download files command.
	 */
	@PostMapping("/execute/download")
	@ResponseBody
	private HttpEntity<FileSystemResource> download(DownloadArgs downloadArgs) throws IOException {
		boolean oneFile = downloadArgs.files.size() == 1;
		String basePath = this.fileSystemService.buildKeyIdPath(downloadArgs.key, downloadArgs.id);
		Path downloadFile;
		String filename;

		if (oneFile) {
			downloadFile = this.fileSystemService.getPath(basePath + downloadArgs.files.get(0));
			filename = downloadFile.getFileName().toString();
		} else {
			Path zipFile = this.fileSystemService.createTempFile();
			ZipOutputStream zipOs = new ZipOutputStream(Files.newOutputStream(zipFile));

			for (String file : downloadArgs.files) {
				Path fileToZip = this.fileSystemService.getPath(basePath + file);

				zipOs.putNextEntry(new ZipEntry(fileToZip.getFileName().toString()));
				Files.newInputStream(fileToZip).transferTo(zipOs);
			}

			zipOs.close();
			downloadFile = zipFile;
			filename = "fichiers.zip";

		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

		return new HttpEntity<>(new FileSystemResource(downloadFile), headers);
	}

	/**
	 * File System command can be executed by this controller.
	 */
	private enum FS_COMMAND {
		LIST("list"), RENAME("rename"), CREATE_DIR("createDir"), COPY("copy"),
		DELETE("delete"), MOVE("move"), UPLOAD("upload"), ABORT_UPLOAD("abort"),
		DOWNLOAD("download");

		private final String name;
		FS_COMMAND(String name) { this.name = name; }

		public static FS_COMMAND fromName(String name) {
			for (FS_COMMAND cmd : FS_COMMAND.values()) {
				if (cmd.name.equals(name)) {
					return cmd;
				}
			}

			throw new IllegalArgumentException();
		}
	}

	/**
	 * Converter for FS_COMMAND.
	 */
	@Component
	static class StringToEnumConverter implements Converter<String, FS_COMMAND> {

		@Override
		public FS_COMMAND convert(String s) {
			return FS_COMMAND.fromName(s);
		}
	}

	/**
	 * POJO for upload command.
	 */
	@Data
	static class MultipartArgs {
		String key;
		String id;
		String dest;
		String name;
		Long size;
		ChunkData chunkData;

		@Data
		static class ChunkData {
			String uploadId;
			Integer index;
			Integer totalCount;
		}
	}

	/**
	 * POJO for download command.
	 */
	@Data
	static class DownloadArgs {
		String key;
		String id;
		List<String> files;
	}

}
