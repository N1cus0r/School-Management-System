import {Input, InputGroup, InputLeftElement} from "@chakra-ui/react";
import {Search2Icon} from "@chakra-ui/icons";
import {useRef} from "react";

interface Props {
  handleSearch: (searchText: string) => void;
}

const SearchUserInput = ({ handleSearch }: Props) => {
  const ref = useRef<HTMLInputElement>(null);

  return (
    <form
      onSubmit={(event) => {
        event.preventDefault();
        if (ref.current) handleSearch(ref.current.value);
      }}
    >
      <InputGroup>
        <InputLeftElement pointerEvents="none">
          <Search2Icon color="gray.300" />
        </InputLeftElement>
        <Input type="text" placeholder="Full Name" ref={ref} />
      </InputGroup>
    </form>
  );
};
export default SearchUserInput;
