package ct.af.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;

import com.google.gson.Gson;

import ct.af.message.incoming.parameter.Param_SDF_GetResourceInventory;
import ct.af.resourceModel.ResourceInventory;
import ec02.af.utils.AFLog;

public class ResourceInventoryFromFile {
	public void writeBackUpResourceInven(String data) {
		DateTime date = new DateTime();
		String name = "ResourceInventory.json." + Config.getRequestIdDate().print(date);

		AFLog.d("================ Write Backup ResourceInventory ================");
		AFLog.d("File name  : "+ name);
		AFLog.d("Path : " + Config.getPATH_BACKUPRESOURCEINVENTORY());

		// Directory create
		directoryCreate(Config.getPATH_BACKUPRESOURCEINVENTORY());

		// file create
		fileCreate(name, data, Config.getPATH_BACKUPRESOURCEINVENTORY());
		//AFLog.d("================ END Load ResourceInventory ================");

	}

	public void directoryCreate(String path) {
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdirs()) {
				AFLog.d("Directory is created :" + Config.getPATH_BACKUPRESOURCEINVENTORY());
			} else {
				AFLog.e("[Error] Failed to create directory :" + Config.getPATH_BACKUPRESOURCEINVENTORY());
			}
		}
	}

	public void fileCreate(String fileName, String data, String pathDirectory) {
		String nameFile =  pathDirectory + fileName;
		Path path = Paths.get(nameFile);
		try {
			Files.write(path, data.getBytes());
			AFLog.d("Backup ResourceInventory is created! : " + fileName);
			
		} catch (Exception e) {
			AFLog.d("Exception create file Backup ResourceInventory :" + e);
		}
	}


	
	public void fileDelete(String fileName, String pathDirectory) {
		String nameFile = pathDirectory +"ResourceInventory.json." + fileName;
		AFLog.d("Before Delete old file :" + nameFile);
		try {
			File file = new File(pathDirectory + fileName);
			if (file.delete()) {
				AFLog.d(file.getName() + " is deleted!");
			} else {
				AFLog.d("Delete operation is failed.");
			}
		} catch (Exception e) {
			AFLog.d("Exception delete file Backup ResourceInventory :" + e);
		}

	}

	public ArrayList<String> listFilesByFolder() {
		ArrayList<String> listFolder = new ArrayList<String>();
		File folder = new File(Config.getPATH_BACKUPRESOURCEINVENTORY());
//		AFLog.d("================ START LIST FILE ================");
		for (final File fileEntry : folder.listFiles()) {
			listFolder.add(fileEntry.getName());
		}
//		AFLog.d(listFolder.toString());
//		AFLog.d("================ END LIST FILE ================");

		return listFolder;
	}

	public String getNewFileBackupResourceInven(ArrayList<String> listFolder) {
		return Collections.max(parseArryToLong(changeFrom(listFolder))).toString();
	}

	public String getOldFileBackupResourceInven(ArrayList<String> listFolder) {
		return Collections.min(parseArryToLong(changeFrom(listFolder))).toString();
	}

	public ArrayList<Long> parseArryToLong(ArrayList<String> listFolder) {
		ArrayList<Long> listFolderInt = new ArrayList<Long>();
		Long nameFolderInt = null;
		for (int i = 0; i < listFolder.size(); i++) {
			nameFolderInt = Long.parseLong(listFolder.get(i));
			listFolderInt.add(nameFolderInt);
		}
		return listFolderInt;
	}

	public ArrayList<String> changeFrom(ArrayList<String> listFolder) {
		ArrayList<String> listFolderJSON = new ArrayList<String>();
		String nameFolder = "";
		for (int i = 0; i < listFolder.size(); i++) {
			nameFolder = listFolder.get(i);
			nameFolder = nameFolder.replaceAll(".json", "");
			nameFolder = nameFolder.replaceAll("ResourceInventory.", "");
			listFolderJSON.add(nameFolder);

		}
		return listFolderJSON;
	}

	public Param_SDF_GetResourceInventory doParserFile(String rawData) {
		Param_SDF_GetResourceInventory param = new Param_SDF_GetResourceInventory();
		Gson gson = GsonPool.getGson();

		try {
			param = gson.fromJson(rawData, Param_SDF_GetResourceInventory.class);

		} catch (Exception ex) {
			param.setIsValid(false);
			AFLog.e("[Exception] can't parser ResourceInventory.");
			AFLog.e(ex);
		}

		GsonPool.pushGson(gson);
		return param;
	}

	public void setResourceInventFormFile(Param_SDF_GetResourceInventory param) {
		if (param.getResultData() != null && param.getResultData().size() > 0) {
			for (ResourceInventory inventory : param.getResultData()) {
				Config.getResourceInventoryHashMap().put(String.valueOf(inventory.getResourceName()), inventory);
//				AFLog.d("==============ResourceInventory form file=====================");
//				AFLog.d(Config.getResourceInventoryHashMap().get(inventory.getResourceName()).toString());
			}
//			AFLog.d("========================================================");
		}
	}
}
