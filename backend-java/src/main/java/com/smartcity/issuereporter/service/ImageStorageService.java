package com.smartcity.issuereporter.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smartcity.issuereporter.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageStorageService {
  private final Cloudinary cloudinary;
  private final AppProperties props;

  public ImageStorageService(AppProperties props) {
    this.props = props;
    this.cloudinary = new Cloudinary(ObjectUtils.asMap(
      "cloud_name", props.getCloudinary().getCloudName(),
      "api_key", props.getCloudinary().getApiKey(),
      "api_secret", props.getCloudinary().getApiSecret()
    ));
  }

  public String upload(MultipartFile file) throws IOException {
    Map params = ObjectUtils.asMap(
      "folder", props.getCloudinary().getFolder(),
      "resource_type", "image"
    );
    Map res = cloudinary.uploader().upload(file.getBytes(), params);
    return (String) res.get("secure_url");
  }
}
