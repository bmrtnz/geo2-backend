package fr.microtec.geo2.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
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
			@PathVariable FsCommand command,
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
				String dest = basePath + body.get("dest");
				this.fileSystemService.move(path, dest);
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

		return buildDownload(filename, downloadFile, true);
	}



	/**
	 * Download etiquette file.
	 */
	@GetMapping("/{type}/{filename}")
	@ResponseBody
	public HttpEntity<FileSystemResource> getDocument(@PathVariable FsDocumentType type, @PathVariable String filename) {
		filename = new String(Base64.getDecoder().decode(filename));
		Path downloadFile;

		if (FsDocumentType.ETIQUETTE.equals(type)) {
			downloadFile = this.fileSystemService.getEtiquette(filename);
		} else if (FsDocumentType.DOCUMENT.equals(type)) {
			downloadFile = this.fileSystemService.getDocument(type.getPath(), filename, true);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return buildDownload(downloadFile.getFileName().toString(), downloadFile, false);
	}

	/**
	 * Build HttpResponse with file to download and correct file name in header.
	 *
	 * @param filename The name of file to download.
	 * @param downloadFile The path of file to download.
	 * @return HttpEntity with file to download.
	 */
	static HttpEntity<FileSystemResource> buildDownload(String filename, Path downloadFile, boolean forceDownload) {
		HttpHeaders headers = new HttpHeaders();

		try {
			headers.setContentType(MediaType.parseMediaType(Files.probeContentType(downloadFile)));
		} catch (IOException | InvalidMediaTypeException e) {
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		}

		if (forceDownload) {
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());
		}

		return new HttpEntity<>(new FileSystemResource(downloadFile), headers);
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
