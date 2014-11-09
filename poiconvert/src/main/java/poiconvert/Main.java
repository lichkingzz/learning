package poiconvert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Main {

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException {
		boolean watchMode = false;
		Map<String, Convertor> paths = new HashMap<String, Convertor>();
		if (StringUtils.equals(args[0], "-watchMode")) {
			watchMode = true;
		}
		String result = null;
		File backup = null;
		for (String configName : args) {
			if (StringUtils.isBlank(configName)
					|| StringUtils.equals(configName, "-watchMode")) {
				continue;
			}
			Convertor convertor = Convertor.createByConfigFile(new File(
					configName));
			if (watchMode) {
				File sourceFolder = new File(convertor.getConfig().get(
						"sourceFolder"));
				Path path = sourceFolder.toPath();
				if (sourceFolder.exists() && sourceFolder.isDirectory()) {
					paths.put(path.toFile().getAbsolutePath(), convertor);
					backup = new File(sourceFolder, "backup");
					backup.mkdirs();
				}
			}
			result = convertor.convert();
			if (watchMode && StringUtils.equals(result, "运行成功")) {
				backupsource(backup, convertor.getResultMap());
			}
			System.out.println(result);
		}
		if (!watchMode) {
			return;
		}

		WatchService watcher = FileSystems.getDefault().newWatchService();
		System.out.println("启动监控模式");
		System.out.println("监控目录列表：");
		for (String pathName : paths.keySet()) {
			Path path = new File(pathName).toPath();
			System.out.println(path.getFileName());
			path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		}

		while (true) {
			WatchKey key = watcher.take();
			List<WatchEvent<?>> watchEvents = key.pollEvents();
			Set<Path> parents = new HashSet<Path>();
			for (WatchEvent<?> watchEvent : watchEvents) {
				Path newFile = (Path) watchEvent.context();
				// 还原父目录
				Path parent = (Path) key.watchable();
				parents.add(parent);
				System.out.println(parent.getFileName() + " 新增文件："
						+ newFile.getFileName());
			}
			for (Path path : parents) {
				Convertor convertor = paths
						.get(path.toFile().getAbsolutePath());
				result = convertor.convert();
				File sourceFolder = new File(convertor.getConfig().get(
						"sourceFolder"));
				backup = new File(sourceFolder, "backup");
				if (StringUtils.equals(result, "运行成功")) {
					backupsource(backup, convertor.getResultMap());
				}
				System.out.println(result);
			}
			key.reset();
		}
	}

	private static void backupsource(File backup, Map<File, File> resultMap) {
		Set<File> sourceFiles = resultMap.keySet();
		for (File file : sourceFiles) {
			File target = new File(backup, file.getName());
			try {
				Files.move(file.toPath(), target.toPath(),
						StandardCopyOption.ATOMIC_MOVE);
				System.out.println("备份原文件成功：" + file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
