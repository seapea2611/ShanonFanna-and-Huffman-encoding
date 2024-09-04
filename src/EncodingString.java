import java.util.*;

class ShannonFanoNode {
    char character;
    int frequency;
    String code;

    public ShannonFanoNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.code = "";
    }

    public int getFrequency() {
        return frequency;
    }
}

class HuffmanNode {
    char character;
    int frequency;
    HuffmanNode leftChild;
    HuffmanNode rightChild;

    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }
}

public class EncodingString {

    /**
     * MAIN FUNCTION
     **/
    public static void main(String[] args) {
        // Nhập chuỗi từ người dùng
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập vào một chuỗi: ");
        String input = scanner.nextLine();
        scanner.close();

        // Build mã
        Map<Character, String> huffmanCodes = buildHuffmanCodes(input);
        Map<Character, String> shannonFanoCodes = buildShannonFanoCodes(input);

        // entrySet() method: Tạo ra 1 tập hợp chứa các phần tử trong HashMap
        System.out.println("Mã hóa Huffman:");
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // entrySet() method: Tạo ra 1 tập hợp chứa các phần tử trong HashMap
        System.out.println("Mã hóa Shannon-Fano:");
        for (Map.Entry<Character, String> entry : shannonFanoCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        String huffmanEncodedString = encodeString(input, huffmanCodes);
        System.out.println("Chuỗi đã mã hóa Huffman: " + huffmanEncodedString);

        String shannonFanoEncodedString = encodeString(input, shannonFanoCodes);
        System.out.println("Chuỗi đã mã hóa Shannon-Fano: " + shannonFanoEncodedString);

        double entropy = calculateEntropy(input);
        double averageCodeLength = calculateAverageCodeLength(input, shannonFanoCodes);
        double compressionEfficiency = entropy / averageCodeLength;
        double redundancy = 1 - compressionEfficiency;

        System.out.println("Hiệu suất mã hoá: " + compressionEfficiency);
        System.out.println("Dư thừa: " + redundancy);
    }

    /**
     * buildHuffmanCodes FUNCTION: Trả về 1 HashMap chứa ký tự (key) và chuoi mã hoá (value)
     **/
    public static Map<Character, String> buildHuffmanCodes(String input) {
        // frequencyMap: HashMap lưu các ký tự và tần suất xuất hiện
        // getOrDefault() method: Lấy value tương ứng với key nếu đã tồn tại, nếu chưa tồn tại gán bằng defaultValue
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c); // Chuyển chuỗi về ký tự thường và đếm
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        // List lưu các Node trong HashMap
        List<HuffmanNode> nodeList = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            nodeList.add(node);
        }

        buildHuffmanTree(nodeList);

        Map<Character, String> codes = new HashMap<>();
        generateHuffmanCodes(nodeList.get(0), "", codes);

        return codes;
    }

    /**
     * buildHuffmanTree FUNCTION: Tạo cây tu các Node trong List
     */
    public static void buildHuffmanTree(List<HuffmanNode> nodeList) {
        while (nodeList.size() > 1) {
            // Sắp xếp List từ bé -> lớn theo tần suất
            nodeList.sort(Comparator.comparingInt(node -> node.frequency));
//            nodeList.sort((node1, node2) -> Integer.compare(node1.frequency, node2.frequency));
            // Tạo node trái, node phải
            HuffmanNode leftChild = nodeList.get(0);
            HuffmanNode rightChild = nodeList.get(1);

            // Node cha bằng tong tần suất của con trái và phải
            HuffmanNode parent = new HuffmanNode('\0', leftChild.frequency + rightChild.frequency);
            parent.leftChild = leftChild;
            parent.rightChild = rightChild;

            // Xoá 2 node đã xét
            nodeList.remove(0);
            nodeList.remove(0);

            // Thêm node cha sau khi tạo
            nodeList.add(parent);
        }
    }

    /**
     * generateHuffmanCodes FUNCTION: Đánh chỉ số trái 0, phải 1 cho cây
     */
    public static void generateHuffmanCodes(HuffmanNode root, String currentCode, Map<Character, String> codes) {
        if (root == null) {
            return;
        }

        // Nếu là nút lá mới thêm toàn bộ chuỗi mã hoá vào trong HashMap codes
        if (root.isLeaf()) {
            char lowercaseChar = Character.toLowerCase(root.character);
            codes.put(lowercaseChar, currentCode);
        }
    
        // Đánh số theo thứ tự pre-order
        generateHuffmanCodes(root.leftChild, currentCode + "0", codes);
        generateHuffmanCodes(root.rightChild, currentCode + "1", codes);
    }

    /**
     * buildShannonFanoCodes FUNCTION: Trả về HashMap chứa ký tự và chuỗi mã hoá
     */
    public static Map<Character, String> buildShannonFanoCodes(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Đếm tần số và lưu vào HashMap
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        // Lưu các phần tử trong HashMap vào mảng
        List<ShannonFanoNode> nodeList = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            char character = entry.getKey();
            int frequency = entry.getValue();
            ShannonFanoNode node = new ShannonFanoNode(character, frequency);
            nodeList.add(node);
        }

        buildShannonFanoTree(nodeList);

        // HashMap codes: Lưu ký tự (key) và chuoi mã hoá (value)
        Map<Character, String> codes = new HashMap<>();
        assignShannonFanoCodes(nodeList, "", codes);

        return codes;
    }

    /**
     * buildShannonFanoTree FUNCTION: Dựng cây
     */
    public static void buildShannonFanoTree(List<ShannonFanoNode> nodeList) {
        // Sắp xếp các nút theo tần suất giảm dần
//        nodeList.sort((node1, node2) -> Integer.compare(node2.frequency, node1.frequency));
        sortNodesByFrequency((nodeList));

        // Tạo stack và thêm List vào trong stack, stack ban đầu chỉ chứa 1 List
        // push() method: Thêm các phần tử vào đỉnh stack
        Stack<List<ShannonFanoNode>> stack = new Stack<>();
        stack.push(nodeList);

        while (!stack.isEmpty()) {
            // pop method(): Lấy phần tử ở đỉnh (Là List) và xoá phần tử đó ra khỏi stack
            List<ShannonFanoNode> nodes = stack.pop();

            // Nếu kích cỡ của List được lấy ra chỉ có 1 phần tử, bỏ qua phần chia theo tần suất
            if (nodes.size() <= 1) {
                continue;
            }

            // Tính tổng tần suất
            int sumFrequency = calculateTotalFrequency(nodes);

            // Tìm phần tử để chia List
            int mid = findSplitIndex(nodes, sumFrequency);

            // subList(int fromIndex, int toIndex) method: trả về List, các phần tử
            // List 1: Từ đầu tới mid
            List<ShannonFanoNode> group1 = nodes.subList(0, mid);

            // List 2: Từ mid tới size của List ban đầu
            List<ShannonFanoNode> group2 = nodes.subList(mid, nodes.size());

            // Mã hoá cho các phần tử có trong List 1 thêm 0
            for (ShannonFanoNode node : group1) {
                node.code += "1";
            }

            // Má hoá cho các phần tử có trong List 2 thêm 1
            for (ShannonFanoNode node : group2) {
                node.code += "0";
            }

            // Thêm 2 List vào stack để tiếp tục chia
            stack.push(group1);
            stack.push(group2);
        }
    }

    /**
     * calculateTotalFrequency FUNCTION: Tính tổng tần suất xuất hiện trong 1 List
     */
    public static int calculateTotalFrequency(List<ShannonFanoNode> nodeList) {
        int sumFrequency = 0;
        for (ShannonFanoNode node : nodeList) {
            sumFrequency += node.frequency;
        }
        return sumFrequency;
    }

    /**
     * findSplitIndex FUNCTION: Tìm phần tử chia List thành 2 phần có frequency tương đương nhau
     */
    public static int findSplitIndex(List<ShannonFanoNode> nodeList, int sumFrequency) {
        int cumulativeFrequency = 0;
        int mid = 0;

        // Gán cumulativeFrequency = 0, cộng lần lượt các frequency và so sánh với tổng tần suất / 2
        for (int i = 0; i < nodeList.size(); i++) {
            ShannonFanoNode node = nodeList.get(i);
            cumulativeFrequency += node.frequency;

            // Nếu < sumFrequency -> Tìm được vị trí tại i
            if (cumulativeFrequency <= sumFrequency / 2) {
                mid = i;
            } else {
                break;
            }
        }

        // subList(int fromIndex, int toIndex): List được tạo tới toIndex - 1 nến mid phải + 1
        return mid + 1;
    }

    /**
     * assignShannonFanoCodes FUNCTION: Duyệt các ký tự có trong List, cập nhật chuỗi mã hoá trong List, thêm vào HashMap
     */
    public static void assignShannonFanoCodes(List<ShannonFanoNode> nodeList, String code, Map<Character, String> codes) {
        for (ShannonFanoNode node : nodeList) {
            node.code = code + node.code;
            codes.put(node.character, node.code);
        }
    }

    /**
     * encodeString FUNCTION: Chuyển toàn bộ chuỗi được nhập thành chuỗi được mã hoa
     */
    public static String encodeString(String input, Map<Character, String> codes) {
        StringBuilder encodedString = new StringBuilder();

        for (char c : input.toCharArray()) {
            // Chuỗi input chứa cả ký tự hoa và thường nhưng trong HashMap chỉ lưu thường nên phải lowerCase
            char lowercaseChar = Character.toLowerCase(c);
            String code = codes.get(lowercaseChar); // get() method: Lấy value với key tương ứng trong HashMap
            if (code != null) {
                encodedString.append(code);
            }
        }

        return encodedString.toString();
    }

    /**
     * calculateEntropy FUNCTION: Tính entropy
     * Entropy = - ∑ (p_i * log2(p_i))
     */
    public static double calculateEntropy(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Đếm tần suất xuất hiện của các ký tự
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        int totalCharacters = input.length();
        double entropy = 0;

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            // probability: Tính xác suất từ HashMap
            double probability = (double) entry.getValue() / totalCharacters;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    /**
     * calculateAverageCodeLength FUNCTION: Tính độ dài mã hoá trung bình
     * Average Code Length = ∑ (p_i * l_i), p_i = số lần xuất hiện / độ dài chuỗi
     */
    public static double calculateAverageCodeLength(String input, Map<Character, String> codes) {
        int totalCharacters = input.length();
        double averageCodeLength = 0;

        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            String code = codes.get(lowercaseChar); // Lấy số lần được mã hoá * độ dài chuỗi mã hoá
            if (code != null) {
                averageCodeLength += code.length(); // Chia cho độ dài chuỗi
            }
        }

        return averageCodeLength / totalCharacters;
    }

    /**
     * sortNodesByFrequency FUNCTION: Sắp xếp nổi bọt tần suất 2 node theo thứ tự giảm dần
     */
    public static void sortNodesByFrequency(List<ShannonFanoNode> nodeList) {
        for (int i = 0; i < nodeList.size() - 1; i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                ShannonFanoNode node1 = nodeList.get(i);
                ShannonFanoNode node2 = nodeList.get(j);

                if (node2.getFrequency() > node1.getFrequency()) {
                    // Hoán đổi vị trí của hai nút
                    Collections.swap(nodeList, i, j);
                }
            }
        }
    }
}
