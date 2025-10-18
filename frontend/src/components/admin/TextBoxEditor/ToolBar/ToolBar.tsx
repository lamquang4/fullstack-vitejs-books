import { memo } from "react";
import { Editor } from "@tiptap/react";
import ButtonTool from "./ButtonTool";
import BlockStyleTool from "./BlockStyleTool";
import LinkTool from "./LinkTool";
import ImageTool from "./ImageTool";
import TableTool from "./TableTool";

function ToolBar({ editor }: { editor: Editor | null }) {

  return (
    <div className="border border-gray-300 border-b-0 bg-gray-50 w-full">
      <div className="flex items-center flex-wrap gap-x-0.5">
        <BlockStyleTool editor={editor} />

        <ButtonTool editor={editor} />

        <ImageTool editor={editor} />

        <LinkTool editor={editor} />

        <TableTool editor={editor} />
      </div>
    </div>
  );
}

export default memo(ToolBar);
