<?php

/**
 * Created by PhpStorm.
 * User: briankrupp
 * Date: 2/28/17
 * Time: 12:27 PM
 */

class fileSystemManager
{
    private $fileDirectory = "/var/www/html/skunkworks/eventImages/";
    private $thumbnailExtension = "-thmb";
    private $mp4Extension = ".mp4";
    private $initialExtension = ".h264";

    public function getFilePath($id) {
        return $this->fileDirectory . $id . $this->mp4Extension;
    }

    public function getFile($id) {
        return file_get_contents($this->fileDirectory . $id . $this->mp4Extension);
    }

    public function getThumbnail($id) {
        return file_get_contents($this->fileDirectory . $id . $this->thumbnailExtension);
    }

    public function createVideo($id, $contents) {
        // First save h264 file
        $initialFile = $this->fileDirectory . $id . $this->initialExtension;
        $destinationFile = $this->fileDirectory . $id . $this->mp4Extension;

        file_put_contents($initialFile, $contents);

        // Convert from h264 to mp4
        $command = "/usr/bin/MP4Box";
        $completeCommand = $command . " -add " . $initialFile . " " . $destinationFile;
        exec($completeCommand, $output, $status);

        // Cleanup
        unlink($initialFile);
    }

    public function storeThumbnail($id, $contents) {
        $thumbnailPath = $this->fileDirectory . $id . $this->thumbnailExtension;
        file_put_contents($thumbnailPath, $contents);
    }

    public function createThumbnail($id) {
        $thumbnailPath = $this->fileDirectory . $id . $this->thumbnailExtension;
        $destinationFile = $this->fileDirectory . $id . $this->mp4Extension;

        $command = "/usr/bin/ffmpeg";
	$completeCommand = "$command -itsoffset -4 -i " . $destinationFile . " -vcodec mjpeg -vframes 1 -an -f rawvideo -s 320x240 " . $this->fileDirectory . $id . $this->thumbnailExtension;
	exec($completeCommand, $output, $status);
    }


    public function deleteFile($id) {
        unlink($this->fileDirectory . $id . $this->mp4Extension);
        unlink($this->fileDirectory . $id . $this->thumbnailExtension);

        return true;
    }

    public function deleteAllFiles() {
        $files = scandir($this->fileDirectory);
        foreach($files as $file) {
            $fullPath = $this->fileDirectory . $file;
            if(is_file($fullPath)) {
                unlink($fullPath);
            }
        }
    }
}
