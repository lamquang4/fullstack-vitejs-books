type Props = {
  closeMenu?: () => void;
  IndexForZ: number;
  children?: React.ReactNode;
};
function Overplay({ closeMenu, IndexForZ, children }: Props) {
  const handleOverlayClick = () => {
    if (closeMenu) closeMenu();
  };
  return (
    <div
      className="fixed inset-0 flex flex-col gap-10 items-center justify-center text-center bg-black/50"
      style={{ zIndex: IndexForZ }}
      onClick={handleOverlayClick}
    >
      {children}
    </div>
  );
}

export default Overplay;
