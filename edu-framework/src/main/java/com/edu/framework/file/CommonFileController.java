package com.edu.framework.file;

import com.edu.common.core.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class CommonFileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "path", required = false) String path) {
        String url = fileService.upload(file, path);
        return R.ok(url);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public R<Boolean> delete(@RequestParam("url") String url) {
        boolean result = fileService.delete(url);
        return R.ok(result);
    }
}
