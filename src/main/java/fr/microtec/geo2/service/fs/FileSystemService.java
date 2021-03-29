package fr.microtec.geo2.service.fs;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service is used for access to file system.
 *
 * All methods take String path will be resolve with _getBasePath function.
 */
@Service
public class FileSystemService {

	protected String basePath = "/";

	/**
	 * List relative given String path.
	 */
	public List<Path> list(String path) {
		return this.list(this._getBasePath(path));
	}

	/**
	 * List absolute given Path path.
	 */
	@SneakyThrows
	public List<Path> list(Path path) {
		if (!Files.exists(path)) {
			return Collections.emptyList();
		}

		return Files.list(path).collect(Collectors.toList());
	}

	/**
	 * Rename relative given String path with name.
	 */
	@SneakyThrows
	public Path rename(String path, String name) {
		Path from = this._getBasePath(path);
		Path to = from.resolveSibling(name);

		return Files.move(from, to);
	}

	/**
	 * Move relative given String path to relative String destination.
	 */
	@SneakyThrows
	public Path move(String path, String dest) {
		return this.move(this._getBasePath(path), dest);
	}

	/**
	 * Move absolute given Path path to relative String destination.
	 */
	@SneakyThrows
	public Path move(Path from, String dest) {
		Path to = this._getBasePath(dest);

		return Files.move(from, to);
	}

	/**
	 * Move absolute given Path path to relative String destination with name (rename file).
	 */
	@SneakyThrows
	public Path move(Path from, String dest, String name) {
		Path toFolder = this._getBasePath(dest);
		Path to = toFolder.resolve(name);

		if (!Files.exists(toFolder)) {
			Files.createDirectories(toFolder);
		}

		return Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Save bytes to relative given String path with name and option.
	 */
	public Path save(String path, String name, byte[] bytes, OpenOption... options) {
		Path file = this._getBasePath(path).resolve(name);

		return this.save(file, bytes, options);
	}

	/**
	 * Save bytes to absolute given Path path and option.
	 */
	@SneakyThrows
	public Path save(Path path, byte[] bytes, OpenOption... options) {
		if (!Files.exists(path)) {
			Files.createFile(path);
		}

		return Files.write(path, bytes, options);
	}

	/**
	 * Save bytes to relative given String path with name and WRITE open option.
	 */
	@SneakyThrows
	public Path save(String path, String name, byte[] bytes) {
		return this.save(path, name, bytes, StandardOpenOption.WRITE);
	}

	/**
	 * Create temp file.
	 */
	@SneakyThrows
	public Path createTempFile() {
		return Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
	}

	/**
	 * Create directory with name in relative given String path.
	 */
	@SneakyThrows
	public Path createDirectory(String path, String name) {
		return Files.createDirectory(this._getBasePath(path).resolve(name));
	}

	/**
	 * Copy relative given String file path to relative String destDir.
	 */
	@SneakyThrows
	public Path copy(String path, String destDir) {
		Path fromPath = this._getBasePath(path);
		Path destPath = this._getBasePath(destDir);

		if (fromPath.toFile().isFile()) {
			destPath = destPath.resolve(fromPath.getFileName());
		}

		return Files.copy(fromPath, destPath);
	}

	/**
	 * Delete relative given String file path.
	 */
	public void delete(String path) {
		this.delete(this._getBasePath(path));
	}

	/**
	 * Delete absolute given Path file path.
	 */
	@SneakyThrows
	public void delete(Path path) {
		Files.delete(path);
	}

	/**
	 * Convert java list of path to list of FileSystemItem.
	 */
	public List<FileSystemItem> toFileSystemItem(List<Path> paths) {
		return paths.stream().map(this::toFileSystemItem).collect(Collectors.toList());
	}

	/**
	 * Convert java Path to FileSystemItem.
	 */
	@SneakyThrows
	public FileSystemItem toFileSystemItem(Path path) {
		BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

		if (attributes.isDirectory()) {
			boolean hasSubDirectory = Files.list(path).anyMatch(child -> child.toFile().isDirectory());

			return new FileSystemFolder(path.getFileName().toString(), attributes.isDirectory(), hasSubDirectory);
		}

		return new FileSystemFile(path.getFileName().toString(), false, attributes.size(), attributes.lastModifiedTime().toInstant());
	}

	/**
	 * Resolve given String path.
	 *
	 * @return Resolved path.
	 */
	protected Path _getBasePath(String path) {
		return Path.of(this.basePath, path);
	}

	/**
	 * Public path resolving.
	 */
	public Path getPath(String path) {
		return this._getBasePath(path);
	}

}
