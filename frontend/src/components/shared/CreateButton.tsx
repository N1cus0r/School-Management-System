import {Button} from "@chakra-ui/react";

interface Props {
    children: string;
    onClick: () => void
}

const CreateButton = ({children, onClick}: Props) => {
    return (
        <Button
            px={6}
            fontSize={"sm"}
            rounded={"full"}
            bg={"blue.400"}
            color={"white"}
            boxShadow={
                "0px 1px 25px -5px rgb(66 153 225 / 48%), 0 10px 10px -5px rgb(66 153 225 / 43%)"
            }
            _hover={{
                bg: "blue.500",
            }}
            _focus={{
                bg: "blue.500",
            }}
            onClick={onClick}
        >
            {children}
        </Button>
    )
}
export default CreateButton
