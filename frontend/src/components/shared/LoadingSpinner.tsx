import {Flex, Spinner} from "@chakra-ui/react";

const LoadingSpinner = () => {
    return (
        <Flex alignItems="center" justifyContent="center">
            <Spinner size="xl" />
        </Flex>
    )
}
export default LoadingSpinner
