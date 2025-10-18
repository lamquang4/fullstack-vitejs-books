type Props = {
  closeMenu?: () => void;
  children?: React.ReactNode;
};
function Overplay({ closeMenu, children }: Props) {
  const handleOverlayClick = () => {
    if (closeMenu) closeMenu();
  };

  return (
    <div
      className={`fixed inset-0 flex flex-col gap-10 items-center justify-center text-center bg-black/50 z-22 ${
        !children ? "xl:hidden" : ""
      }`}
      onClick={handleOverlayClick}
    >
      {children}
    </div>
  );
}

export default Overplay;