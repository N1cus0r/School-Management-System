import {ReactNode} from "react";
import {SimpleGrid} from "@chakra-ui/react";

interface Props {
    children: ReactNode[];
}
const CardsContainer = ({children}: Props) => {
    return (
        <SimpleGrid columns={{ sm: 2, md: 3, lg: 4, xl: 5 }}>{children}</SimpleGrid>
    )
}
export default CardsContainer
