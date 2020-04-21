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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * @author tom
 *
 */
@SpringBootTest
@ActiveProfiles("test")
public class PhotoAlbumServiceTest {

	private static Path sourcepath1;
	private static Path sourcepath2;
	private static Path targetpath;

	private AlbumParams params;

	@Autowired
	private PhotoAlbumService underTest;

	@Autowired
	private ImageRepository imageRepository;

	@Value("${photoalbum.prefix}")
	private String PREFIX;

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
		params = new AlbumParams();
		params.addSource(sourcepath1.toString());
		params.addSource(sourcepath2.toString());
		params.setTarget(targetpath.toString());
		params.setDebug(false);
	}

	@Test
	public void testCreateFileList() {
		List<File> fileList = underTest.createFilteredFileList(params);
		assertNotNull(fileList);
		assertTrue("no images found", !fileList.isEmpty());

		boolean filterSuccessful = true;
		for (File thisFile : fileList) {
			if (thisFile.isDirectory() || !FilenameUtils.getExtension(thisFile.getName()).equalsIgnoreCase("jpg")) {
				filterSuccessful = false;
			}
		}
		assertTrue("encountered error in filtering", filterSuccessful);
	}

	@Test
	public void testCopyFiles() throws IOException {
		List<File> fileList = underTest.createFilteredFileList(params);
		Map<Image, File> fileMap = underTest.createFileMap(fileList);
		Map<File, Image> targetFileMap = underTest.createTargetFiles(fileMap);
		File tempDir = targetFileMap.get(targetFileMap.keySet().iterator().next()).getTempFile().getParentFile();
		File targetDir = targetpath.toFile();
		underTest.copyFiles(targetFileMap, targetDir);
		assertEquals(targetDir.listFiles().length, targetFileMap.size());
		assertFalse(tempDir.exists());
	}

	@Test
	@Transactional
	public void testCreateOrUpdatePhotoAlbum() {
		imageRepository.deleteAll();
		assertTrue("table image is not empty", imageRepository.findAll().isEmpty());
		assertFalse(params.isDebug());
		List<Image> imageList = underTest.createOrUpdatePhotoAlbum(params);
		assertEquals(2, imageList.size());
		for(Image thisImage : imageList) {
			assertNotNull(thisImage.getTempFile());
		}
		imageRepository.saveAll(imageList);
		for(Image imageFromDb : imageRepository.findAll()) {
			assertTrue("error in DB", imageFromDb.getCreationDate().equals(imageList.get(0).getCreationDate()) ||
							imageFromDb.getCreationDate().equals(imageList.get(1).getCreationDate()));
			assertNull(imageFromDb.getTempFile());
			assertTrue("wrong filename", imageFromDb.getFilename().startsWith(PREFIX));
		}
	}

}