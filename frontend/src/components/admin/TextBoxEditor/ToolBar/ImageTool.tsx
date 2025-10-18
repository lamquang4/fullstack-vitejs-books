import { Editor } from "@tiptap/react";
import { memo, useRef, useState } from "react";
import { FaRegImage } from "react-icons/fa";
import { TbCloudUpload } from "react-icons/tb";

function ImageTool({ editor }: { editor: Editor | null }) {
  const [openImage, setOpenImage] = useState<boolean>(false);
  const inputRef = useRef<HTMLInputElement | null>(null);

  const toggleImage = () => {
    setOpenImage((prev) => !prev);
  };

  const triggerImageUpload = () => {
    inputRef.current?.click();
  };

  const handleAddImageUrl = () => {
    const url = window.prompt("URL image");

    if (url) {
      editor?.chain().focus().setImage({ src: url }).run();
    }
  };

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    e.target.value = "";

    if (url) {
      editor?.chain().focus().setImage({ src: url }).run();
    }
  };

  const imageTools = [
    {
      label: (
        <>
          <TbCloudUpload size={18} /> <span>Upload</span>
          <input
            type="file"
            accept=".png,.jpg,.webp"
            ref={inputRef}
            className="hidden"
            onChange={handleImageUpload}
          />
        </>
      ),
      onClick: triggerImageUpload,
      active: false,
    },
    {
      label: (
        <>
          <FaRegImage size={18} /> <span>By URL</span>
        </>
      ),
      onClick: handleAddImageUrl,
      active: false,
    },
  ];

  return (
    <div
      className="relative"
      onMouseEnter={toggleImage}
      onMouseLeave={toggleImage}
    >
      <button
        type="button"
        title="Image"
        className="p-2 flex items-center justify-between hover:bg-gray-100 bg-gray-50"
      >
        <FaRegImage size={18} />
      </button>

      {openImage && (
        <div className="absolute left-1/2 -translate-x-1/2 top-full min-w-max bg-white z-6 border border-gray-200 shadow-md">
          {imageTools.map((tool, index) => (
            <button
              key={`image-tool-${index}`}
              type="button"
              onClick={tool.onClick}
              className={`flex items-center w-full p-2 gap-1.5 bg-gray-50 
                       hover:bg-gray-100
                      `}
            >
              {tool.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default memo(ImageTool);
