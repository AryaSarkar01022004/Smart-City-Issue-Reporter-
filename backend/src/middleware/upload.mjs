import multer from "multer"

const storage = multer.memoryStorage()

export const upload = multer({
  storage,
  limits: {
    fileSize: 6 * 1024 * 1024, // 6MB
    files: 1,
  },
  fileFilter: (req, file, cb) => {
    if (!file.mimetype.startsWith("image/")) {
      return cb(new Error("Only image uploads are allowed"))
    }
    cb(null, true)
  },
})
