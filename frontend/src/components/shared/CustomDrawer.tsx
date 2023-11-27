import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay
} from "@chakra-ui/react";
import {ReactNode} from "react";

interface Props {
    isOpen: boolean,
    onClose: () => void
    heading: string
    children: ReactNode
}

const CustomDrawer = ({isOpen, onClose, heading, children}: Props) => {
    return (
        <Drawer isOpen={isOpen} onClose={onClose} placement="right" size="lg">
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>{heading}</DrawerHeader>
                <DrawerBody>
                    {children}
                </DrawerBody>
                <DrawerFooter borderTopWidth="1px">
                    <Button variant="outline" mr={3} onClick={onClose}>
                        Cancel
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    )
}
export default CustomDrawer
