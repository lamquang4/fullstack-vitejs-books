import { useCallback, useEffect, useState } from "react";

export const useInputImage1 = () => {
  const [previewImages1, setPreviewImages1] = useState<string[][]>([]);
  const [selectedFiles1, setSelectedFiles1] = useState<File[][]>([]);

  useEffect(() => {
    return () => {
      previewImages1.forEach((block) =>
        block.forEach((url) => URL.revokeObjectURL(url))
      );
    };
  }, [previewImages1]);

  const onFileSelect = useCallback(
    (file: File, blockIndex: number, imageIndex: number) => {
      setPreviewImages1((prev) => {
        const updated = [...prev];
        updated[blockIndex] = updated[blockIndex] || [];
        if (updated[blockIndex][imageIndex]) {
          URL.revokeObjectURL(updated[blockIndex][imageIndex]);
        }
        updated[blockIndex][imageIndex] = URL.createObjectURL(file);
        return updated;
      });

      setSelectedFiles1((prev) => {
        const updated = [...prev];
        updated[blockIndex] = updated[blockIndex] || [];
        updated[blockIndex][imageIndex] = file;
        return updated;
      });
    },
    []
  );

  const handleClear = useCallback((blockIndex: number, imageIndex: number) => {
    setPreviewImages1((prev) => {
      const updated = [...prev];
      if (updated[blockIndex]?.[imageIndex]) {
        URL.revokeObjectURL(updated[blockIndex][imageIndex]);
        updated[blockIndex].splice(imageIndex, 1);
      }
      return updated;
    });

    setSelectedFiles1((prev) => {
      const updated = [...prev];
      if (updated[blockIndex]?.[imageIndex]) {
        updated[blockIndex].splice(imageIndex, 1);
      }
      return updated;
    });
  }, []);

  return {
    previewImages1,
    setPreviewImages1,
    selectedFiles1,
    setSelectedFiles1,
    onFileSelect,
    handleClear,
  };
};
