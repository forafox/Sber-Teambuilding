import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { YMaps, Map, Placemark } from "react-yandex-maps";

type Props = {
  position: number[];
  open: boolean;
  onOpenChange: (open: boolean) => void;
};

export function MapDialog({ position, open, onOpenChange }: Props) {
  const handlePathTo = () => {};

  return (
    <Dialog
      open={open}
      onOpenChange={(open) => {
        onOpenChange(open);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Редактирование события</DialogTitle>
          <DialogDescription>
            Вы можете изменить название события, дату и участников.
          </DialogDescription>
        </DialogHeader>
        <DialogDescription className="h-[200px]">
          <YMaps>
            <Map
              defaultState={{
                center: position,
                zoom: 10,
              }}
              width="100%"
              height="200px"
            >
              <Placemark
                geometry={position}
                properties={{
                  hintContent: "Моя метка",
                  balloonContent: "Точка назначения",
                }}
              />
            </Map>
          </YMaps>
        </DialogDescription>
        <DialogFooter>
          <Button onClick={handlePathTo}>Построить маршрут</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
