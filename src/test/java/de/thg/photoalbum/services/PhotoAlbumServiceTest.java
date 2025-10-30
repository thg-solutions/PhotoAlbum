package de.thg.photoalbum.services;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tom
 *
 */
@SpringBootTest
public class PhotoAlbumServiceTest {

	private static Path sourcepath1;
	private static Path sourcepath2;
	private static Path targetpath;

	private AlbumParams params;

	@Inject
	private PhotoAlbumService underTest;

	@Inject
	private ImageRepository imageRepository;

	@BeforeAll
	static void beforeAll() throws IOException {
		targetpath = Files.createTempDirectory("target_");
		sourcepath1 = Files.createTempDirectory("source1_");
		sourcepath2 = Files.createTempDirectory("source2_");
		File destFile1 = new File(sourcepath1.toFile(), "PHOTO0021.JPG");
		File destFile2 = new File(sourcepath2.toFile(), "PHOTO0083.JPG");
		Files.copy(PhotoAlbumServiceTest.class.getResourceAsStream("/testdata/PHOTO0021.JPG"), destFile1.toPath());
		Files.copy(PhotoAlbumServiceTest.class.getResourceAsStream("/testdata/PHOTO0083.JPG"), destFile2.toPath());

	}

	@AfterAll
	static void afterAll() {
		File target = targetpath.toFile();
		FileUtils.deleteQuietly(target);
		File source1 = sourcepath1.toFile();
		FileUtils.deleteQuietly(source1);
		File source2 = sourcepath2.toFile();
		FileUtils.deleteQuietly(source2);
	}

	@BeforeEach
	public void setUp() {
		params = new AlbumParams(targetpath.toString(), List.of(sourcepath1.toString(), sourcepath2.toString()), false);
	}

	@Test
	public void testCreateFileList() {
		List<File> fileList = underTest.createFilteredFileList(params);
		assertThat(fileList).isNotNull();
		assertThat(fileList).as("no images found").isNotEmpty();

		boolean filterSuccessful = true;
		for (File thisFile : fileList) {
			if (thisFile.isDirectory() || !FilenameUtils.getExtension(thisFile.getName()).equalsIgnoreCase("jpg")) {
				filterSuccessful = false;
			}
		}
		assertThat(filterSuccessful).as("encountered error in filtering").isTrue();
	}

	@Test
	public void testCopyFiles() throws IOException {
		List<File> fileList = underTest.createFilteredFileList(params);
		Map<Image, File> fileMap = underTest.createFileMap(fileList);
		Map<File, Image> targetFileMap = underTest.createTargetFiles(fileMap);
		File tempDir = targetFileMap.get(targetFileMap.keySet().iterator().next()).getTempFile().getParentFile();
		File targetDir = targetpath.toFile();
		underTest.copyFiles(targetFileMap, targetDir);
		assertThat(targetDir).exists();
		assertThat(targetFileMap).hasSameSizeAs(targetDir.listFiles());
		assertThat(tempDir).doesNotExist();
	}


}